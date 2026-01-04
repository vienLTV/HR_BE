package org.microboy.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.microboy.constants.ExceptionConstants;
import org.microboy.dto.request.AttendanceCheckInRequestDTO;
import org.microboy.dto.request.AttendanceCheckOutRequestDTO;
import org.microboy.dto.response.AttendanceDashboardSummaryDTO;
import org.microboy.dto.response.AttendanceResponseDTO;
import org.microboy.dto.response.PaginatedResponse;
import org.microboy.entity.AttendanceEntity;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Attendance Module
 */
@ApplicationScoped
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

	@Override
	@Transactional
	public AttendanceResponseDTO checkIn(UUID employeeId, UUID organizationId, AttendanceCheckInRequestDTO request) {
		log.debug("Starting checkIn process for employeeId: {} orgId: {}", employeeId, organizationId);

		if (employeeId == null) {
			log.error("employeeId parameter is null");
			throw new BadRequestException("Employee ID is required");
		}
		if (organizationId == null) {
			log.error("organizationId parameter is null");
			throw new BadRequestException("Organization ID is required");
		}

		// Verify employee exists and belongs to the organization
		EmployeeCoreEntity employee = EmployeeCoreEntity.findById(employeeId);
		if (employee == null) {
			log.error("Employee not found: employeeId={}", employeeId);
			throw new EntityNotFoundException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
		}

		log.debug("Employee found: employeeId={}, employee.organizationId={}", employeeId, employee.organizationId);

		if (employee.organizationId == null) {
			log.error("Employee has null organizationId: employeeId={}", employeeId);
			throw new BadRequestException("Employee organization is not set - data integrity error");
		}

		if (!organizationId.equals(employee.organizationId)) {
			log.warn("Organization mismatch: JWT organizationId={}, employee.organizationId={}",
				organizationId, employee.organizationId);
			throw new BadRequestException("Employee does not belong to your organization");
		}

		LocalDate today = LocalDate.now();
		LocalDateTime checkInTime = request.getCheckInTime() != null ? request.getCheckInTime() : LocalDateTime.now();

		log.debug("Checking for existing attendance: organizationId={}, employeeId={}, date={}",
			organizationId, employeeId, today);

		// Check if there's already an attendance record for today
		AttendanceEntity existingAttendance = AttendanceEntity.findByOrgAndEmployeeAndDate(organizationId, employeeId, today);
		
		if (existingAttendance != null) {
			if (existingAttendance.checkOutTime == null) {
				// There's an active check-in (not checked out yet)
				log.warn("Duplicate check-in attempt: employeeId={}, date={}", employeeId, today);
				throw new BadRequestException("You have already checked in today. Please check out first.");
			} else {
				// Already checked out today - update the record for re-check-in
				log.info("Re-check-in after checkout: updating attendance record for employeeId={}, date={}", employeeId, today);
				existingAttendance.checkInTime = checkInTime;
				existingAttendance.checkOutTime = null;
				existingAttendance.status = AttendanceStatus.PENDING;
				if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
					existingAttendance.notes = request.getNotes();
				}
				existingAttendance.persist();
				log.info("Employee {} re-checked in successfully at {}", employeeId, checkInTime);
				return convertToDTO(existingAttendance);
			}
		}

		// Create new attendance record (first check-in of the day)
		AttendanceEntity attendanceEntity = AttendanceEntity.builder()
			.organizationId(organizationId)
			.employeeId(employeeId)
			.attendanceDate(today)
			.checkInTime(checkInTime)
			.checkOutTime(null)
			.status(AttendanceStatus.PENDING)
			.notes(request.getNotes())
			.build();

		AttendanceEntity.persist(attendanceEntity);
		log.info("Employee {} checked in successfully at {} for organization {}", employeeId, checkInTime, organizationId);

		return convertToDTO(attendanceEntity);
	}

	@Override
	@Transactional
	public AttendanceResponseDTO checkOut(UUID employeeId, UUID organizationId, AttendanceCheckOutRequestDTO request) {
		if (employeeId == null) {
			throw new BadRequestException("Employee ID is required");
		}
		if (organizationId == null) {
			throw new BadRequestException("Organization ID is required");
		}

		// Verify employee exists and belongs to the organization
		EmployeeCoreEntity employee = EmployeeCoreEntity.findById(employeeId);
		if (employee == null) {
			throw new EntityNotFoundException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
		}
		if (!organizationId.equals(employee.organizationId)) {
			throw new BadRequestException("Employee does not belong to your organization");
		}

		LocalDate today = LocalDate.now();

		// Find today's attendance record
		AttendanceEntity attendanceEntity = AttendanceEntity.findByOrgAndEmployeeAndDate(organizationId, employeeId, today);
		if (attendanceEntity == null) {
			throw new BadRequestException("No check-in found for today. Please check in first.");
		}

		// Check if already checked out
		if (attendanceEntity.checkOutTime != null) {
			throw new BadRequestException("You have already checked out today.");
		}

		// Update attendance record
		LocalDateTime checkOutTime = request.getCheckOutTime() != null ? request.getCheckOutTime() : LocalDateTime.now();
		attendanceEntity.checkOutTime = checkOutTime;
		attendanceEntity.status = AttendanceStatus.PRESENT;
		if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
			attendanceEntity.notes = request.getNotes();
		}

		log.info("Employee {} checked out at {}", employeeId, checkOutTime);

		return convertToDTO(attendanceEntity);
	}

	@Override
	public PaginatedResponse<AttendanceResponseDTO> getMyAttendance(UUID employeeId, UUID organizationId, int page, int pageSize) {
		if (employeeId == null) {
			throw new BadRequestException("Employee ID is required");
		}
		if (organizationId == null) {
			throw new BadRequestException("Organization ID is required");
		}

		// Verify employee exists and belongs to the organization
		EmployeeCoreEntity employee = EmployeeCoreEntity.findById(employeeId);
		if (employee == null) {
			throw new EntityNotFoundException(ExceptionConstants.EMPLOYEE_NOT_FOUND);
		}
		if (!organizationId.equals(employee.organizationId)) {
			throw new BadRequestException("Employee does not belong to your organization");
		}

		// Get paginated attendance records
		var attendanceQuery = AttendanceEntity.findByOrgAndEmployee(organizationId, employeeId);
		List<AttendanceEntity> attendanceEntities = attendanceQuery.page(page, pageSize).list();
		long totalItems = attendanceQuery.count();
		int totalPages = (int) Math.ceil((double) totalItems / pageSize);

		// Convert to DTOs
		List<AttendanceResponseDTO> attendanceDTOs = new ArrayList<>();
		attendanceEntities.forEach(entity -> attendanceDTOs.add(convertToDTO(entity)));

		PaginatedResponse<AttendanceResponseDTO> response = new PaginatedResponse<>();
		response.setItems(attendanceDTOs);
		response.setTotalItems(totalItems);
		response.setTotalPages(totalPages);
		response.setCurrentPage(page);
		response.setPageSize(pageSize);

		return response;
	}

	@Override
	public AttendanceDashboardSummaryDTO getDashboardSummary(UUID organizationId) {
		if (organizationId == null) {
			throw new BadRequestException("Organization ID is required");
		}

		// Count total employees in organization
		long totalEmployees = EmployeeCoreEntity.findEmployeesByOrgId(organizationId).count();

		// Count attendance records for today with status PRESENT or PENDING
		LocalDate today = LocalDate.now();
		long totalAttendanceToday = AttendanceEntity.find(
			"organizationId = ?1 AND attendanceDate = ?2 AND (status = ?3 OR status = ?4)",
			organizationId, today, AttendanceStatus.PRESENT, AttendanceStatus.PENDING
		).count();

		return AttendanceDashboardSummaryDTO.builder()
			.totalEmployees(totalEmployees)
			.totalAttendanceToday(totalAttendanceToday)
			.build();
	}

	private AttendanceResponseDTO convertToDTO(AttendanceEntity entity) {
		return AttendanceResponseDTO.builder()
			.attendanceId(entity.attendanceId)
			.organizationId(entity.organizationId)
			.employeeId(entity.employeeId)
			.attendanceDate(entity.attendanceDate)
			.checkInTime(entity.checkInTime)
			.checkOutTime(entity.checkOutTime)
			.status(entity.status)
			.notes(entity.notes)
			.build();
	}
}

