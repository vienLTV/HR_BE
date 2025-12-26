package org.microboy.service;

import org.microboy.dto.request.EmployeeCoreRequestDTO;
import org.microboy.dto.EmployeeOverviewDTO;
import org.microboy.dto.response.EmployeeCoreResponseDTO;
import org.microboy.dto.response.PaginatedResponse;
import org.microboy.security.entity.UserEntity;

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

	/**
	 * DEV/TEST ONLY: Ensures an EmployeeCoreEntity exists for the given user.
	 * If user.employeeId exists, returns it. Otherwise, creates a minimal EmployeeCoreEntity
	 * and links it to the user account.
	 *
	 * @param user The UserEntity to ensure has an employee record
	 * @return UUID of the employee (existing or newly created)
	 */
	UUID ensureEmployeeExistsForUser(UserEntity user);
}
