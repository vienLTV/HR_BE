package org.microboy.service;

import org.microboy.dto.PersonalDetailDTO;

import java.util.UUID;

public interface PersonalDetailService {
	PersonalDetailDTO createPersonalDetail(PersonalDetailDTO personalDetailDTO);
	PersonalDetailDTO updatePersonalDetail(PersonalDetailDTO personalDetailDTO, UUID employeeId);
	PersonalDetailDTO getPersonalDetailById(UUID id);
}
