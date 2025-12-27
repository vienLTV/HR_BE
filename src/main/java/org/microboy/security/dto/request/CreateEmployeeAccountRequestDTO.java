package org.microboy.security.dto.request;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.microboy.security.enums.Role;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class CreateEmployeeAccountRequestDTO {

	private UUID employeeId;
	private String accountEmail;
	private String password;
	private Role role;
}
