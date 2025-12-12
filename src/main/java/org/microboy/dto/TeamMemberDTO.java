package org.microboy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.microboy.enums.TeamRole;

import java.util.UUID;

@Getter
@Setter
@Builder
@RegisterForReflection
public class TeamMemberDTO {
	private UUID teamId;
	private UUID employeeId;
	private TeamRole role;
}
