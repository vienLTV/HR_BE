package org.microboy.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.dto.request.LeaveRequestCreateDTO;
import org.microboy.dto.request.LeaveRequestStatusUpdateDTO;
import org.microboy.dto.response.LeaveRequestResponseDTO;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.entity.LeaveRequestEntity;
import org.microboy.enums.LeaveStatus;
import org.microboy.repository.LeaveRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class LeaveRequestServiceImpl implements LeaveRequestService {

	private final LeaveRequestRepository leaveRequestRepository;

	@Override
	@Transactional
	public LeaveRequestResponseDTO createLeaveRequest(UUID employeeId, UUID organizationId, LeaveRequestCreateDTO dto) {
		log.info("Creating leave request for employee: {}", employeeId);

		// Validation
		if (dto.getFromDate().isAfter(dto.getToDate())) {
			throw new BadRequestException("From date cannot be after to date");
		}

		// Create entity
		LeaveRequestEntity entity = new LeaveRequestEntity();
		entity.employeeId = employeeId;
		entity.organizationId = organizationId;
		entity.fromDate = dto.getFromDate();
		entity.toDate = dto.getToDate();
		entity.reason = dto.getReason();
		entity.status = LeaveStatus.PENDING;
		entity.createdAt = LocalDateTime.now();

		leaveRequestRepository.persist(entity);
		log.info("Leave request created with ID: {}", entity.leaveRequestId);

		return mapToDTO(entity);
	}

	@Override
	public List<LeaveRequestResponseDTO> getMyLeaveRequests(UUID employeeId, UUID organizationId) {
		log.info("Fetching leave requests for employee: {}", employeeId);
		List<LeaveRequestEntity> entities = leaveRequestRepository.findByEmployeeId(employeeId);
		return entities.stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<LeaveRequestResponseDTO> getAllLeaveRequests(UUID organizationId) {
		log.info("Fetching all leave requests for organization: {}", organizationId);
		List<LeaveRequestEntity> entities = leaveRequestRepository.findByOrganizationId(organizationId);
		return entities.stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public LeaveRequestResponseDTO updateLeaveRequestStatus(UUID leaveRequestId, UUID approverId, LeaveRequestStatusUpdateDTO dto) {
		log.info("Updating leave request {} status to {}", leaveRequestId, dto.getStatus());

		LeaveRequestEntity entity = leaveRequestRepository.findById(leaveRequestId);
		if (entity == null) {
			throw new EntityNotFoundException("Leave request not found");
		}

		if (entity.status != LeaveStatus.PENDING) {
			throw new BadRequestException("Can only update status of pending leave requests");
		}

		entity.status = dto.getStatus();
		entity.approvedBy = approverId;
		entity.approvedAt = LocalDateTime.now();
		entity.updatedAt = LocalDateTime.now();

		leaveRequestRepository.persist(entity);
		log.info("Leave request status updated successfully");

		return mapToDTO(entity);
	}

	@Override
	public LeaveRequestResponseDTO getLeaveRequestById(UUID leaveRequestId) {
		LeaveRequestEntity entity = leaveRequestRepository.findById(leaveRequestId);
		if (entity == null) {
			throw new EntityNotFoundException("Leave request not found");
		}
		return mapToDTO(entity);
	}

	private LeaveRequestResponseDTO mapToDTO(LeaveRequestEntity entity) {
		LeaveRequestResponseDTO dto = new LeaveRequestResponseDTO();
		dto.setLeaveRequestId(entity.leaveRequestId);
		dto.setEmployeeId(entity.employeeId);
		dto.setFromDate(entity.fromDate);
		dto.setToDate(entity.toDate);
		dto.setReason(entity.reason);
		dto.setStatus(entity.status);
		dto.setApprovedBy(entity.approvedBy);
		dto.setApprovedAt(entity.approvedAt);
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

		// Get approver name
		if (entity.approvedBy != null) {
			try {
				EmployeeCoreEntity approver = EmployeeCoreEntity.findById(entity.approvedBy);
				if (approver != null) {
					dto.setApprovedByName(approver.firstName + " " + approver.lastName);
				}
			} catch (Exception e) {
				log.warn("Failed to fetch approver name for ID: {}", entity.approvedBy);
			}
		}

		return dto;
	}
}
