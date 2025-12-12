package org.microboy.service;

import org.microboy.dto.request.SignUpRequestDTO;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.entity.OrganizationEntity;
import org.microboy.enums.AccountStatus;
import org.microboy.security.dto.UserDTO;
import org.microboy.security.enums.Role;
import org.microboy.security.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class SignUpServiceImpl implements SignUpService {
	private final UserService userService;

	@Override
	@Transactional
	public void createOrgAndOwnerAccount(SignUpRequestDTO signUpRequestDTO) {
		if (signUpRequestDTO == null) {
			throw new IllegalArgumentException("request can not be null");
		}

		// Create organization
		OrganizationEntity organizationEntity = new OrganizationEntity();
		organizationEntity.name = signUpRequestDTO.getOrganizationName();
		OrganizationEntity.persist(organizationEntity);

		// Create employee core
		EmployeeCoreEntity employeeCoreEntity = new EmployeeCoreEntity();
		employeeCoreEntity.companyEmail = signUpRequestDTO.getAccountEmail();
		employeeCoreEntity.firstName = signUpRequestDTO.getFirstName();
		employeeCoreEntity.lastName = signUpRequestDTO.getLastName();
		employeeCoreEntity.organizationId = organizationEntity.organizationId;
		EmployeeCoreEntity.persist(employeeCoreEntity);

		// Create user (Owner's account)
		UserDTO userDTO = new UserDTO();
		userDTO.setAccountEmail(signUpRequestDTO.getAccountEmail());
		userDTO.setPassword(signUpRequestDTO.getPassword());
		userDTO.setOrganizationId(organizationEntity.organizationId);
		userDTO.setRole(Role.OWNER);
		userDTO.setAccountStatus(AccountStatus.ACTIVE);
		userDTO.setEmployeeId(employeeCoreEntity.employeeId);
		userService.createUser(userDTO);

		log.info("Created organization account: {}", signUpRequestDTO.getOrganizationName());
	}
}
