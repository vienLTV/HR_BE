package org.microboy.service;

import org.microboy.dto.request.SignUpRequestDTO;

public interface SignUpService {
	void createOrgAndOwnerAccount(SignUpRequestDTO signUpRequestDTO);
}
