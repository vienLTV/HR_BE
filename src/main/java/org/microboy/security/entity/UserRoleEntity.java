package org.microboy.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.microboy.security.enums.Role;

import java.util.UUID;

@Entity
@Table(name = "users_roles")
@Getter
@Setter
public class UserRoleEntity {

	@Id
	@UuidGenerator
	@Column(name = "user_role_id")
	private UUID id;

	@Column(name = "account_email")
	private String accountEmail;

	@Column(name = "role_name")
	@Enumerated(EnumType.STRING)
	private Role roleName;
}
