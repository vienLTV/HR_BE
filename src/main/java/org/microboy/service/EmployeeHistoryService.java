package org.microboy.service;

import org.microboy.dto.request.EmployeeHistoryRequestDTO;
import org.microboy.dto.response.EmployeeHistoryResponseDTO;
import org.microboy.dto.response.PaginatedResponse;

import java.util.UUID;

public interface EmployeeHistoryService {

    EmployeeHistoryResponseDTO createEmployeeHistory(EmployeeHistoryRequestDTO employeeHistoryDTO);

    EmployeeHistoryResponseDTO updateEmployeeHistory(EmployeeHistoryRequestDTO employeeHistoryDTO, UUID id);

    PaginatedResponse<EmployeeHistoryResponseDTO> findAllEmployeeHistoryByEmployeeId(UUID employeeId, int page, int pageSize);

    EmployeeHistoryResponseDTO findEmployeeHistoryById(UUID id);

    void deleteEmployeeHistoryById(UUID id);

}
