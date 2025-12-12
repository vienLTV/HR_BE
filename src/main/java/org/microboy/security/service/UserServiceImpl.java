package org.microboy.security.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.microboy.entity.EmployeeCoreEntity;
import org.microboy.enums.AccountStatus;
import org.microboy.security.dto.AuthRequest;
import org.microboy.security.dto.AuthResponse;
import org.microboy.security.dto.UserDTO;
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
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;
	private final PBKDF2Encoder passwordEncoder;
	private final TokenUtils tokenUtils;

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
				                                        userEntity.getOrganizationId(), roles, duration, issuer);
				authResponse.setToken(token);
				authResponse.setAccountEmail(userEntity.getAccountEmail());
				authResponse.setRole(userEntity.getRole());

				EmployeeCoreEntity employee = EmployeeCoreEntity.findById(userEntity.employeeId);
				if (employee != null) {
					authResponse.setEmployeeId(employee.employeeId);
					authResponse.setFirstName(employee.firstName);
					authResponse.setLastName(employee.lastName);
				}
				return authResponse;
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}

	private UserDTO convertToUserDTO(UserEntity userEntity) {
		UserDTO userDTO = new UserDTO();
		userDTO.setAccountEmail(userEntity.getAccountEmail());
		userDTO.setRole(userEntity.getRole());
		userDTO.setAccountStatus(userEntity.getAccountStatus());
		return userDTO;
	}
}
