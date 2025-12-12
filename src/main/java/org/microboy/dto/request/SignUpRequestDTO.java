package org.microboy.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDTO {
	private String firstName;
	private String lastName;
	private String organizationName;
	private String accountEmail;
	private String password;
}
