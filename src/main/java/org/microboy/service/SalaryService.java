package org.microboy.service;

import org.microboy.dto.request.CalculateSalaryRequestDTO;
import org.microboy.dto.response.SalaryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface SalaryService {

	/**
	 * Get salary records for logged-in employee
	 */
	List<SalaryResponseDTO> getMySalary(UUID employeeId, UUID organizationId);

	/**
	 * Get salary records for team members (MANAGER role)
	 */
	List<SalaryResponseDTO> getTeamSalary(UUID managerId, UUID organizationId);

	/**
	 * Get all salary records in organization (ADMIN/OWNER role)
	 */
	List<SalaryResponseDTO> getAllSalary(UUID organizationId);

	/**
	 * Calculate salary for all employees in organization for a specific period
	 */
	List<SalaryResponseDTO> calculateSalary(UUID organizationId, CalculateSalaryRequestDTO dto);

	/**
	 * Mark a salary record as paid
	 */
	SalaryResponseDTO markAsPaid(UUID salaryId, UUID organizationId);

	/**
	 * Get salary by ID
	 */
	SalaryResponseDTO getSalaryById(UUID salaryId);
}
