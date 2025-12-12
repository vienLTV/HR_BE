package org.microboy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RegisterForReflection
public class TeamDTO {
	private UUID teamId;
	private String name;
	private String description;
	private UUID departmentId;
	private String email;
	private String phoneNumber;
	private String location;
	private String establishedDate;
}
