package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.microboy.enums.MaritalStatus;
import org.microboy.enums.Title;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited
@Table(name = "personal_detail")
public class PersonalDetailEntity extends PanacheEntityBase {

	@Id
	@Column(name = "employee_id")
	public UUID employeeId;

	@Column(name = "first_name")
	public String firstName;

	@Column(name = "last_name")
	public String lastName;

	@Column(name = "preferred_name")
	public String preferredName;

	@Column(name = "personal_email")
	public String personalEmail;

	@Column(name = "personal_phone_number")
	public String personalPhoneNumber;

	@Column(name = "date_of_birth")
	public LocalDate dateOfBirth;

	@Column(name = "current_address")
	public String currentAddress;

	@Column(name = "birth_place")
	public String birthPlace;

	@Column(name= "marital_status")
	@Enumerated(EnumType.STRING)
	public MaritalStatus maritalStatus;

	@Column(name= "title")
	@Enumerated(EnumType.STRING)
	public Title title;
}
