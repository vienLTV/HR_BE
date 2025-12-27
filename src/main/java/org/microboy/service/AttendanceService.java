package org.microboy.service;

import org.microboy.dto.request.AttendanceCheckInRequestDTO;
import org.microboy.dto.request.AttendanceCheckOutRequestDTO;
import org.microboy.dto.response.AttendanceDashboardSummaryDTO;
import org.microboy.dto.response.AttendanceResponseDTO;
import org.microboy.dto.response.PaginatedResponse;

import java.util.UUID;

public interface AttendanceService {

	AttendanceResponseDTO checkIn(UUID employeeId, UUID organizationId, AttendanceCheckInRequestDTO request);

	AttendanceResponseDTO checkOut(UUID employeeId, UUID organizationId, AttendanceCheckOutRequestDTO request);

	PaginatedResponse<AttendanceResponseDTO> getMyAttendance(UUID employeeId, UUID organizationId, int page, int pageSize);

	AttendanceDashboardSummaryDTO getDashboardSummary(UUID organizationId);
}

