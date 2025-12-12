package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organization")
public class OrganizationEntity extends PanacheEntityBase {
	@Id
	@UuidGenerator
	@Column(name = "organization_id")
	public UUID organizationId;

	@Column(name = "name")
	public String name;

	@Column(name = "description")
	public String description;

	@Column(name = "created_at")
	public LocalDateTime createdAt;

	@Column(name = "owner_account")
	public String owner;
}
