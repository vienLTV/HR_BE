package org.microboy.security.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.microboy.security.enums.Role;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@RegisterForReflection
public class AuthResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = 1512390957271591509L;

	private String firstName;
	private String lastName;
	private String accountEmail;
	private String token;
	private UUID employeeId;
	private Role role;
}
