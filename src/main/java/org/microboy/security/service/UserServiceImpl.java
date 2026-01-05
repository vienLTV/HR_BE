package org.microboy.security.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.enums.AccountStatus;
import org.microboy.security.dto.AuthRequest;
import org.microboy.security.dto.AuthResponse;
import org.microboy.security.config.OrganizationContext;
import org.microboy.security.dto.UserDTO;
import org.microboy.security.dto.request.ChangePasswordRequestDTO;
import org.microboy.security.dto.request.CreateEmployeeAccountRequestDTO;
import org.microboy.security.entity.UserEntity;
import org.microboy.security.entity.UserRoleEntity;
import org.microboy.security.enums.Role;
import org.microboy.security.repository.UserRepository;
import org.microboy.security.repository.UserRoleRepository;
import org.microboy.security.utils.PBKDF2Encoder;
import org.microboy.security.utils.TokenUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;
	private final PBKDF2Encoder passwordEncoder;
	private final TokenUtils tokenUtils;
	private final OrganizationContext organizationContext;

	@ConfigProperty(name = "com.microboy.cetus.jwt.duration")
	Long duration;

	@ConfigProperty(name = "com.microboy.cetus.jwt.verify.issuer")
	String issuer;

	@Override
	@Transactional
	public UserDTO createUser(UserDTO userDTO) {
		UserEntity userEntity = userRepository.findById(userDTO.getAccountEmail());
		if (userEntity != null) {
			throw new EntityExistsException("username already exists");
		} else {
			userEntity = new UserEntity();
		}

		userEntity.setAccountEmail(userDTO.getAccountEmail());
		userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		userEntity.setAccountStatus(userDTO.getAccountStatus());
		userEntity.setOrganizationId(userDTO.getOrganizationId());
		userEntity.setEmployeeId(userDTO.getEmployeeId());
		userRepository.persist(userEntity);

		UserRoleEntity userRoleEntity = new UserRoleEntity();
		userRoleEntity.setAccountEmail(userEntity.getAccountEmail());
		userRoleEntity.setRoleName(userDTO.getRole());
		userRoleRepository.persist(userRoleEntity);
		userEntity.setRole(userDTO.getRole());

		return convertToUserDTO(userEntity);
	}

	@Override
	public UserEntity findById(String email) {
		UserEntity userEntity = userRepository.findById(email);
		if (userEntity != null) {
			List<UserRoleEntity> userRoleEntities = userRoleRepository.findUserRoleByAccountEmail(userEntity.getAccountEmail());
			Role role = userRoleEntities.stream().map(UserRoleEntity::getRoleName).findFirst().get();
			userEntity.setRole(role);
		}
		return userEntity;
	}

	@Override
	public AuthResponse authenticateUser(AuthRequest authRequest) {
		AuthResponse authResponse = new AuthResponse();
		UserEntity userEntity = findById(authRequest.accountEmail);
		if (userEntity != null &&
		    userEntity.getPassword().equals(passwordEncoder.encode(authRequest.password)) &&
		    AccountStatus.ACTIVE.equals(userEntity.getAccountStatus())) {
			try {
				List<UserRoleEntity> userRoleEntities
						= userRoleRepository.findUserRoleByAccountEmail(userEntity.getAccountEmail());
				Set<Role> roles = userRoleEntities.stream()
				                                    .map(UserRoleEntity::getRoleName)
				                                    .collect(Collectors.toSet());
				String token = tokenUtils.generateToken(userEntity.getAccountEmail(),
				                                        userEntity.getOrganizationId(), roles, duration, issuer, userEntity.getEmployeeId());
				authResponse.setToken(token);
				authResponse.setAccountEmail(userEntity.getAccountEmail());
				authResponse.setRole(userEntity.getRole());

				if (userEntity.getEmployeeId() != null) {
    EmployeeCoreEntity employee =
        EmployeeCoreEntity.findById(userEntity.getEmployeeId());

    if (employee != null) {
        authResponse.setEmployeeId(employee.employeeId);
        authResponse.setFirstName(employee.firstName);
        authResponse.setLastName(employee.lastName);
    }
}

				return authResponse;
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public UserEntity createUserForEmployee(CreateEmployeeAccountRequestDTO request) {
		if (request == null) {
			throw new BadRequestException("Request cannot be null");
		}
		if (request.getEmployeeId() == null) {
			throw new BadRequestException("employeeId is required");
		}
		if (request.getAccountEmail() == null || request.getAccountEmail().trim().isEmpty()) {
			throw new BadRequestException("accountEmail is required");
		}
		if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
			throw new BadRequestException("password is required");
		}
		if (request.getRole() == null) {
			throw new BadRequestException("role is required");
		}
		if (Role.OWNER.equals(request.getRole())) {
			throw new BadRequestException("Cannot create account with role OWNER via this endpoint");
		}

		EmployeeCoreEntity employee = EmployeeCoreEntity.findById(request.getEmployeeId());
		if (employee == null) {
			throw new BadRequestException("Employee not found with id: " + request.getEmployeeId());
		}

		// Ensure employee belongs to current organization
		if (organizationContext.getCurrentOrganizationId() == null) {
			throw new BadRequestException("Organization context is missing");
		}
		if (employee.organizationId != null && !employee.organizationId.equals(organizationContext.getCurrentOrganizationId())) {
			throw new BadRequestException("Employee does not belong to current organization");
		}

		UserEntity existingByEmail = userRepository.findById(request.getAccountEmail());
		if (existingByEmail != null) {
			throw new EntityExistsException("Account email already exists: " + request.getAccountEmail());
		}

		UserEntity existingByEmployee = userRepository.find("employeeId", request.getEmployeeId()).firstResult();
		if (existingByEmployee != null) {
			throw new BadRequestException("Employee already linked to account: " + existingByEmployee.getAccountEmail());
		}

		UserEntity userEntity = new UserEntity();
		userEntity.setAccountEmail(request.getAccountEmail());
		userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
		userEntity.setAccountStatus(AccountStatus.ACTIVE);
		userEntity.setEmployeeId(request.getEmployeeId());
		userEntity.setOrganizationId(employee.organizationId != null
			? employee.organizationId
			: organizationContext.getCurrentOrganizationId());

		userRepository.persist(userEntity);

		UserRoleEntity userRoleEntity = new UserRoleEntity();
		userRoleEntity.setAccountEmail(userEntity.getAccountEmail());
		userRoleEntity.setRoleName(request.getRole());
		userRoleRepository.persist(userRoleEntity);

		userEntity.setRole(request.getRole());
		return userEntity;
	}

	@Override
	@Transactional
	public void changePassword(java.util.UUID employeeId, ChangePasswordRequestDTO request) {
		if (employeeId == null) {
			throw new BadRequestException("Employee ID is required");
		}
		if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
			throw new BadRequestException("Current password is required");
		}
		if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
			throw new BadRequestException("New password is required");
		}
		if (request.getNewPassword().length() < 6) {
			throw new BadRequestException("New password must be at least 6 characters");
		}

		// Find user by employeeId
		UserEntity userEntity = userRepository.find("employeeId", employeeId).firstResult();
		if (userEntity == null) {
			throw new BadRequestException("User not found");
		}

		// Verify current password
		String encodedCurrentPassword = passwordEncoder.encode(request.getCurrentPassword());
		if (!userEntity.getPassword().equals(encodedCurrentPassword)) {
			throw new BadRequestException("Current password is incorrect");
		}

		// Update password
		userEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.persist(userEntity);
	}

	private UserDTO convertToUserDTO(UserEntity userEntity) {
		UserDTO userDTO = new UserDTO();
		userDTO.setAccountEmail(userEntity.getAccountEmail());
		userDTO.setRole(userEntity.getRole());
		userDTO.setAccountStatus(userEntity.getAccountStatus());
		return userDTO;
	}
}
