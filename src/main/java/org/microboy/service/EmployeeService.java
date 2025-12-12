package org.microboy.service;

import org.microboy.dto.request.EmployeeCoreRequestDTO;
import org.microboy.dto.EmployeeOverviewDTO;
import org.microboy.dto.response.EmployeeCoreResponseDTO;
import org.microboy.dto.response.PaginatedResponse;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

	void createEmployee(EmployeeCoreRequestDTO employeeRequest);
	EmployeeCoreRequestDTO updateEmployee(EmployeeCoreRequestDTO employee, UUID id);
	EmployeeOverviewDTO updateEmployeeOverview(EmployeeOverviewDTO employeeOverviewDTO, UUID id);
	PaginatedResponse<EmployeeCoreResponseDTO> findAllEmployeesByPage(int page, int pageSize);
	EmployeeCoreResponseDTO findEmployeeById(UUID employeeId);
	List<EmployeeCoreRequestDTO> findEmployeesByJobId(UUID jobId);
	void deleteEmployeeById(UUID id);
	EmployeeCoreResponseDTO findEmployeeByAccountEmail(String accountEmail);
}
