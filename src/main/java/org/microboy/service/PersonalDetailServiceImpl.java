package org.microboy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.microboy.dto.PersonalDetailDTO;
import org.microboy.entity.PersonalDetailEntity;

import java.util.UUID;

/**
 * @author Khanh Tran
 */
@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class PersonalDetailServiceImpl implements PersonalDetailService {

	private final ObjectMapper objectMapper;

	@Override
	@Transactional
	public PersonalDetailDTO createPersonalDetail(PersonalDetailDTO personalDetailDTO) {
		if (personalDetailDTO == null) {
			throw new IllegalArgumentException("personalDetailDTO is null");
		}

		PersonalDetailEntity personalDetailEntity = objectMapper.convertValue(personalDetailDTO, PersonalDetailEntity.class);
		PersonalDetailEntity.persist(personalDetailEntity);

		log.info("Personal detail created for employee: {}", personalDetailEntity.employeeId);
		return objectMapper.convertValue(personalDetailEntity, PersonalDetailDTO.class);
	}

	@Override
	@Transactional
	public PersonalDetailDTO updatePersonalDetail(PersonalDetailDTO personalDetailDTO,  UUID employeeId) {
		if (personalDetailDTO == null) {
			throw new IllegalArgumentException("personalDetailDTO is null");
		}

		PersonalDetailEntity personalDetailEntity = PersonalDetailEntity.findById(employeeId);
		if (personalDetailEntity == null) {
			throw new BadRequestException("personal detail not found");
		}

		personalDetailEntity.firstName = personalDetailDTO.getFirstName();
		personalDetailEntity.lastName = personalDetailDTO.getLastName();
		personalDetailEntity.personalEmail = personalDetailDTO.getPersonalEmail();
		personalDetailEntity.personalPhoneNumber = personalDetailDTO.getPersonalPhoneNumber();
		personalDetailEntity.birthPlace = personalDetailDTO.getBirthPlace();
		personalDetailEntity.currentAddress = personalDetailDTO.getCurrentAddress();
		personalDetailEntity.dateOfBirth = personalDetailDTO.getDateOfBirth();
		personalDetailEntity.preferredName = personalDetailDTO.getPreferredName();
		personalDetailEntity.title = personalDetailDTO.getTitle();
		personalDetailEntity.maritalStatus = personalDetailDTO.getMaritalStatus();

		log.info("Personal detail updated for employee: {}", personalDetailEntity.employeeId);
		return objectMapper.convertValue(personalDetailEntity, PersonalDetailDTO.class);
	}

	@Override
	public PersonalDetailDTO getPersonalDetailById(UUID id) {
		if (id == null) {
			throw new IllegalArgumentException("id is null");
		}

		PersonalDetailEntity personalDetailEntity = PersonalDetailEntity.findById(id);
		if (personalDetailEntity == null) {
			throw new BadRequestException("personal detail not found");
		}

		return objectMapper.convertValue(personalDetailEntity, PersonalDetailDTO.class);
	}
}
