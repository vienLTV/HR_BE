package org.microboy.security.config;

import jakarta.enterprise.context.RequestScoped;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter @Getter
@RequestScoped
public class OrganizationContext {
	private UUID currentOrganizationId;
}
