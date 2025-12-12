package org.microboy.service;

import java.util.UUID;

import org.microboy.dto.EmployeeProfileDTO;
import org.microboy.entity.EmployeeProfileEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class EmployeeProfileServiceImpl implements EmployeeProfileService {
	private final ObjectMapper objectMapper;

	@Override
	@Transactional
	public void updateEmployeeAvatar(EmployeeProfileDTO employeeProfileDTO) {
		EmployeeProfileEntity employeeProfile = EmployeeProfileEntity.findById(employeeProfileDTO.getEmployeeId());
		if (employeeProfile == null) {
			employeeProfile = new EmployeeProfileEntity();
			employeeProfile.employeeId = employeeProfileDTO.getEmployeeId();
		}

		employeeProfile.avatarImage = employeeProfileDTO.getAvatarImage();
		employeeProfile.avatarFileName = employeeProfileDTO.getAvatarFileName();
		employeeProfile.avatarContentType = employeeProfileDTO.getAvatarContentType();
		employeeProfile.avatarFileSize = (long) employeeProfileDTO.getAvatarImage().length;

		employeeProfile.persist();
	}

	@Override
	@Transactional
	public EmployeeProfileDTO getEmployeeProfileById(UUID employeeId) {
		EmployeeProfileEntity employeeProfile = EmployeeProfileEntity.findById(employeeId);
		if (employeeProfile == null) {
			return null;
		}

		return objectMapper.convertValue(employeeProfile, EmployeeProfileDTO.class);
	}
}
