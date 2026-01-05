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
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.entity.EmployeeHistoryEntity;
import org.microboy.entity.SalaryEntity;
import org.microboy.entity.TeamMemberEntity;
import org.microboy.enums.SalaryStatus;
import org.microboy.repository.SalaryRepository;

import java.math.BigDecimal;
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

			// Calculate bonus and deductions (this is simplified - in real app would be more complex)
			BigDecimal bonus = calculateBonus(employee.employeeId, dto.getMonth(), dto.getYear());
			BigDecimal deductions = calculateDeductions(employee.employeeId, dto.getMonth(), dto.getYear());

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
	private BigDecimal calculateBonus(UUID employeeId, Integer month, Integer year) {
		// Simplified: no bonus calculation yet
		// TODO: Implement based on business rules (attendance, performance, etc.)
		return BigDecimal.ZERO;
	}

	/**
	 * Calculate deductions for employee (simplified logic)
	 * In real application, this would consider insurance, taxes, leaves, etc.
	 */
	private BigDecimal calculateDeductions(UUID employeeId, Integer month, Integer year) {
		// Simplified: no deduction calculation yet
		// TODO: Implement based on business rules (insurance, taxes, unpaid leave, etc.)
		return BigDecimal.ZERO;
	}
}
