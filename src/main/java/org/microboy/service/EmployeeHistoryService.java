package org.microboy.service;

import org.microboy.dto.request.EmployeeHistoryRequestDTO;
import org.microboy.dto.response.EmployeeHistoryResponseDTO;
import java.util.List;

import java.util.UUID;

public interface EmployeeHistoryService {

    EmployeeHistoryResponseDTO createEmployeeHistory(EmployeeHistoryRequestDTO employeeHistoryDTO);

    EmployeeHistoryResponseDTO updateEmployeeHistory(EmployeeHistoryRequestDTO employeeHistoryDTO, UUID id);

    List<EmployeeHistoryResponseDTO> findAllEmployeeHistoryByEmployeeId(UUID employeeId);

    EmployeeHistoryResponseDTO findEmployeeHistoryById(UUID id);

    void deleteEmployeeHistoryById(UUID id);

}
