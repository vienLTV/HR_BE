package org.microboy.service;

import org.microboy.dto.request.LeaveRequestCreateDTO;
import org.microboy.dto.request.LeaveRequestStatusUpdateDTO;
import org.microboy.dto.response.LeaveRequestResponseDTO;

import java.util.List;
import java.util.UUID;

public interface LeaveRequestService {

	LeaveRequestResponseDTO createLeaveRequest(UUID employeeId, UUID organizationId, LeaveRequestCreateDTO dto);

	List<LeaveRequestResponseDTO> getMyLeaveRequests(UUID employeeId, UUID organizationId);

	List<LeaveRequestResponseDTO> getAllLeaveRequests(UUID organizationId);

	LeaveRequestResponseDTO updateLeaveRequestStatus(UUID leaveRequestId, UUID approverId, LeaveRequestStatusUpdateDTO dto);

	LeaveRequestResponseDTO getLeaveRequestById(UUID leaveRequestId);
}
