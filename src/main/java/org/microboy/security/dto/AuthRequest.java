package org.microboy.security.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@RegisterForReflection
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthRequest {

	public String accountEmail;
	public String password;
}
