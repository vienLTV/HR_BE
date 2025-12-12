package org.microboy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RegisterForReflection
public class EmployeeProfileDTO {
	private UUID employeeId;
	private byte[] avatarImage;
	private String avatarFileName;
	private String avatarContentType;
	private Long avatarFileSize;
}
