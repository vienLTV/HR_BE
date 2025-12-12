package org.microboy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class DepartmentDTO {
	private UUID departmentId;
	private String name;
	private String code;
	private String description;
	private UUID managerId;
	private String location;
	private LocalDate establishedDate;
	private String phoneNumber;
	private UUID parentId;
	private String email;
}
