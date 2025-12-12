package org.microboy.security.service;

import org.microboy.security.dto.AuthRequest;
import org.microboy.security.dto.AuthResponse;
import org.microboy.security.dto.UserDTO;
import org.microboy.security.entity.UserEntity;

public interface UserService {

	UserDTO createUser(UserDTO userDTO);
	UserEntity findById(String username);
	AuthResponse authenticateUser(AuthRequest authRequest);
}
