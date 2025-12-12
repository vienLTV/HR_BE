package org.microboy.security.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.microboy.enums.AccountStatus;
import org.microboy.security.enums.Role;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
public class UserDTO {

	private String accountEmail;
	private String password;
	private AccountStatus accountStatus;
	private Role role;
	private UUID organizationId;
	private UUID employeeId;
}
