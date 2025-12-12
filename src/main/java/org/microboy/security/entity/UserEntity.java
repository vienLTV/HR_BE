package org.microboy.security.entity;

import java.util.UUID;

import org.microboy.enums.AccountStatus;
import org.microboy.security.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

	@Id
	@Column(name = "account_email")
	private String accountEmail;

	@Column
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "account_status")
	private AccountStatus accountStatus;

	@Transient
	private Role role;

	@Column(name = "organization_id")
	public UUID organizationId;

	@Column(name = "employee_id")
	public UUID employeeId;

}
