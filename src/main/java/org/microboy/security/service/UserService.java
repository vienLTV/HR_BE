package org.microboy.security.service;

import org.microboy.security.dto.AuthRequest;
import org.microboy.security.dto.AuthResponse;
import org.microboy.security.dto.UserDTO;
import org.microboy.security.dto.request.ChangePasswordRequestDTO;
import org.microboy.security.dto.request.CreateEmployeeAccountRequestDTO;
import org.microboy.security.entity.UserEntity;

import java.util.UUID;

public interface UserService {

	UserDTO createUser(UserDTO userDTO);
	UserEntity findById(String username);
	AuthResponse authenticateUser(AuthRequest authRequest);
	UserEntity createUserForEmployee(CreateEmployeeAccountRequestDTO request);
	void changePassword(UUID employeeId, ChangePasswordRequestDTO request);
}
