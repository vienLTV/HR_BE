package org.microboy.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.dto.request.CalculateSalaryRequestDTO;
import org.microboy.dto.response.SalaryResponseDTO;
import org.microboy.entity.AttendanceEntity;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.entity.EmployeeHistoryEntity;
import org.microboy.entity.LeaveRequestEntity;
import org.microboy.entity.SalaryEntity;
import org.microboy.entity.TeamMemberEntity;
import org.microboy.enums.LeaveStatus;
import org.microboy.enums.SalaryStatus;
import org.microboy.repository.SalaryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class SalaryServiceImpl implements SalaryService {

	private final SalaryRepository salaryRepository;

	private static final int WORKING_DAYS_TARGET = 22;
	private static final BigDecimal ATTENDANCE_BONUS_RATE = new BigDecimal("0.05");

	@Override
	public List<SalaryResponseDTO> getMySalary(UUID employeeId, UUID organizationId) {
		log.info("Fetching salary for employee: {}", employeeId);
		List<SalaryEntity> entities = salaryRepository.findByEmployeeId(employeeId);
		return entities.stream()
				.filter(entity -> entity.organizationId.equals(organizationId))
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<SalaryResponseDTO> getTeamSalary(UUID managerId, UUID organizationId) {
		log.info("Fetching team salary for manager: {}", managerId);
		
		// Get team members managed by this manager
		List<TeamMemberEntity> teamMembers = TeamMemberEntity.find("employeeId", managerId).list();
		if (teamMembers.isEmpty()) {
			log.warn("Manager {} is not part of any team", managerId);
			return List.of();
		}

		// Get all employee IDs in the team(s)
		List<UUID> teamIds = teamMembers.stream()
				.map(tm -> tm.teamId)
				.distinct()
				.collect(Collectors.toList());

		List<UUID> employeeIds = new ArrayList<>();
		for (UUID teamId : teamIds) {
			List<TeamMemberEntity> members = TeamMemberEntity.find("teamId", teamId).list();
			employeeIds.addAll(members.stream()
					.map(tm -> tm.employeeId)
					.collect(Collectors.toList()));
		}

		// Get salary for all team members
		List<SalaryEntity> entities = salaryRepository.findByTeamMembers(employeeIds);
		return entities.stream()
				.filter(entity -> entity.organizationId.equals(organizationId))
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<SalaryResponseDTO> getAllSalary(UUID organizationId) {
		log.info("Fetching all salary for organization: {}", organizationId);
		List<SalaryEntity> entities = salaryRepository.findByOrganizationId(organizationId);
		return entities.stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public List<SalaryResponseDTO> calculateSalary(UUID organizationId, CalculateSalaryRequestDTO dto) {
		log.info("Calculating salary for organization {} - {}/{}", organizationId, dto.getMonth(), dto.getYear());

		// Validation
		if (dto.getMonth() < 1 || dto.getMonth() > 12) {
			throw new BadRequestException("Month must be between 1 and 12");
		}
		if (dto.getYear() < 2020) {
			throw new BadRequestException("Year must be 2020 or later");
		}

		// Get all active employees in organization
		List<EmployeeCoreEntity> employees = EmployeeCoreEntity.find("organizationId", organizationId).list();
		if (employees.isEmpty()) {
			throw new BadRequestException("No employees found in organization");
		}

		List<SalaryResponseDTO> results = new ArrayList<>();

		for (EmployeeCoreEntity employee : employees) {
			// Check if salary already exists for this period
			var existingSalary = salaryRepository.findByEmployeeAndPeriod(
					employee.employeeId, dto.getMonth(), dto.getYear());

			if (existingSalary.isPresent()) {
				log.info("Salary already exists for employee {} for {}/{}", 
						employee.employeeId, dto.getMonth(), dto.getYear());
				continue;
			}

			// Get employee's base salary from latest employment history
			BigDecimal basicSalary = getEmployeeBasicSalary(employee.employeeId);

			// Calculate bonus and deductions with attendance + leave + tax rules
			BigDecimal bonus = calculateBonus(organizationId, employee.employeeId, dto.getMonth(), dto.getYear(), basicSalary);
			BigDecimal deductions = calculateDeductions(organizationId, employee.employeeId, dto.getMonth(), dto.getYear(), basicSalary, bonus);

			// Create salary record
			SalaryEntity salaryEntity = new SalaryEntity();
			salaryEntity.employeeId = employee.employeeId;
			salaryEntity.organizationId = organizationId;
			salaryEntity.month = dto.getMonth();
			salaryEntity.year = dto.getYear();
			salaryEntity.basicSalary = basicSalary;
			salaryEntity.bonus = bonus;
			salaryEntity.deductions = deductions;
			salaryEntity.status = SalaryStatus.PENDING;
			salaryEntity.createdAt = LocalDateTime.now();

			salaryRepository.persist(salaryEntity);
			log.info("Created salary record {} for employee {}", salaryEntity.salaryId, employee.employeeId);

			results.add(mapToDTO(salaryEntity));
		}

		return results;
	}

	@Override
	@Transactional
	public SalaryResponseDTO markAsPaid(UUID salaryId, UUID organizationId) {
		log.info("Marking salary {} as paid", salaryId);

		SalaryEntity entity = salaryRepository.findById(salaryId);
		if (entity == null) {
			throw new EntityNotFoundException("Salary record not found");
		}

		// Verify organization ownership
		if (!entity.organizationId.equals(organizationId)) {
			throw new ForbiddenException("You don't have permission to access this salary record");
		}

		if (entity.status == SalaryStatus.PAID) {
			throw new BadRequestException("Salary already marked as paid");
		}

		entity.status = SalaryStatus.PAID;
		entity.paidAt = LocalDateTime.now();
		entity.updatedAt = LocalDateTime.now();

		salaryRepository.persist(entity);
		log.info("Salary marked as paid successfully");

		return mapToDTO(entity);
	}

	@Override
	public SalaryResponseDTO getSalaryById(UUID salaryId) {
		SalaryEntity entity = salaryRepository.findById(salaryId);
		if (entity == null) {
			throw new EntityNotFoundException("Salary record not found");
		}
		return mapToDTO(entity);
	}

	/**
	 * Helper method to map entity to DTO
	 */
	private SalaryResponseDTO mapToDTO(SalaryEntity entity) {
		SalaryResponseDTO dto = new SalaryResponseDTO();
		dto.setSalaryId(entity.salaryId);
		dto.setEmployeeId(entity.employeeId);
		dto.setMonth(entity.month);
		dto.setYear(entity.year);
		dto.setBasicSalary(entity.basicSalary);
		dto.setBonus(entity.bonus);
		dto.setDeductions(entity.deductions);
		dto.setTotalSalary(entity.totalSalary);
		dto.setStatus(entity.status);
		dto.setPaidAt(entity.paidAt);
		dto.setCreatedAt(entity.createdAt);
		dto.setUpdatedAt(entity.updatedAt);

		// Get employee name
		try {
			EmployeeCoreEntity employee = EmployeeCoreEntity.findById(entity.employeeId);
			if (employee != null) {
				dto.setEmployeeName(employee.firstName + " " + employee.lastName);
			}
		} catch (Exception e) {
			log.warn("Failed to fetch employee name for ID: {}", entity.employeeId);
		}

		return dto;
	}

	/**
	 * Get employee's basic salary from employment history
	 * Returns default if no history found
	 */
	private BigDecimal getEmployeeBasicSalary(UUID employeeId) {
		try {
			// Try to get latest employment record with salary information
			List<EmployeeHistoryEntity> histories = EmployeeHistoryEntity
					.find("employeeId = ?1 AND fieldName = ?2 ORDER BY changedAt DESC", 
							employeeId, "Base Salary")
					.list();

			if (!histories.isEmpty()) {
				String salaryStr = histories.get(0).newValue;
				if (salaryStr != null && !salaryStr.isEmpty()) {
					return new BigDecimal(salaryStr);
				}
			}
		} catch (Exception e) {
			log.warn("Failed to get base salary from history for employee {}: {}", employeeId, e.getMessage());
		}

		// Default basic salary if no history found
		return new BigDecimal("10000000"); // 10 million VND default
	}

	/**
	 * Calculate bonus for employee (simplified logic)
	 * In real application, this would consider performance, attendance, etc.
	 */
	private BigDecimal calculateBonus(UUID organizationId, UUID employeeId, Integer month, Integer year, BigDecimal basicSalary) {
		AttendanceStats stats = computeAttendanceStats(organizationId, employeeId, month, year);

		// Attendance bonus: +5% basic if không có ngày vắng (unpaidDays = 0)
		if (stats.unpaidDays == 0) {
			return basicSalary.multiply(ATTENDANCE_BONUS_RATE).setScale(2, RoundingMode.HALF_UP);
		}

		return BigDecimal.ZERO;
	}

	private BigDecimal calculateDeductions(UUID organizationId, UUID employeeId, Integer month, Integer year,
			BigDecimal basicSalary, BigDecimal bonus) {
		AttendanceStats stats = computeAttendanceStats(organizationId, employeeId, month, year);

		BigDecimal dailyRate = basicSalary
				.divide(BigDecimal.valueOf(WORKING_DAYS_TARGET), 2, RoundingMode.HALF_UP);

		BigDecimal absenceDeduction = dailyRate.multiply(BigDecimal.valueOf(stats.unpaidDays))
				.setScale(2, RoundingMode.HALF_UP);

		// Bỏ thuế thu nhập: chỉ trừ ngày vắng không lương
		return absenceDeduction.setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Tính thống kê chấm công và nghỉ phép cho tháng.
	 */
	private AttendanceStats computeAttendanceStats(UUID organizationId, UUID employeeId, int month, int year) {
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

		List<AttendanceEntity> attendances = AttendanceEntity.find(
				"organizationId = ?1 AND employeeId = ?2 AND attendanceDate BETWEEN ?3 AND ?4",
				organizationId, employeeId, start, end)
				.list();

		long attendanceDays = attendances.stream()
				.filter(a -> a.attendanceDate != null)
				.distinct()
				.count();

		// Leave days (approved) overlapping the month
		List<LeaveRequestEntity> approvedLeaves = LeaveRequestEntity.find(
				"organizationId = ?1 AND status = ?2",
				organizationId, LeaveStatus.APPROVED)
				.list();

		long approvedLeaveDays = approvedLeaves.stream()
				.map(leave -> calculateOverlapDays(leave.fromDate, leave.toDate, start, end))
				.filter(days -> days > 0)
				.mapToLong(Long::longValue)
				.sum();

		int paidDays = (int) Math.min(WORKING_DAYS_TARGET, attendanceDays + approvedLeaveDays);
		int unpaidDays = Math.max(0, WORKING_DAYS_TARGET - paidDays);

		return new AttendanceStats((int) attendanceDays, (int) approvedLeaveDays, unpaidDays);
	}

	private long calculateOverlapDays(LocalDate from, LocalDate to, LocalDate windowStart, LocalDate windowEnd) {
		if (from == null || to == null) {
			return 0;
		}
		LocalDate effectiveStart = from.isBefore(windowStart) ? windowStart : from;
		LocalDate effectiveEnd = to.isAfter(windowEnd) ? windowEnd : to;
		if (effectiveEnd.isBefore(effectiveStart)) {
			return 0;
		}
		return effectiveStart.datesUntil(effectiveEnd.plusDays(1)).count();
	}

	private record AttendanceStats(int attendanceDays, int approvedLeaveDays, int unpaidDays) {}
}
