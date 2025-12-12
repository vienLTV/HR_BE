package org.microboy.service;

import org.microboy.dto.EmployeeProfileDTO;

import java.util.UUID;

public interface EmployeeProfileService {
	void updateEmployeeAvatar(EmployeeProfileDTO employeeProfileDTO);
	EmployeeProfileDTO getEmployeeProfileById(UUID employeeId);
}
