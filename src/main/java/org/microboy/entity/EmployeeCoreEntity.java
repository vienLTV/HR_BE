package org.microboy.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import org.microboy.enums.EmployeeStatus;
import org.microboy.enums.Gender;
import org.microboy.enums.MaritalStatus;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited
@Table(name = "employee_core")
public class EmployeeCoreEntity extends PanacheEntityBase {

	@Id
	@UuidGenerator
	@Column(name = "employee_id")
	public UUID employeeId;

	@Column(name = "first_name")
	public String firstName;

	@Column(name = "last_name")
	public String lastName;

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

	@Column(name= "gender")
	@Enumerated(EnumType.STRING)
	public Gender gender;

	@Column(name = "company_email")
	public String companyEmail;

	@Column(name = "company_phone_number")
	public String companyPhoneNumber;

	@Column(name = "employee_code")
	public String employeeCode;

	@Column(name= "employee_status")
	@Enumerated(EnumType.STRING)
	public EmployeeStatus employeeStatus;

	@Column(name = "job_title_id")
	public UUID jobTitleId;

	@Column(name = "team_id")
	public UUID teamId;

	@Column(name = "organization_id")
	public UUID organizationId;

	public static EmployeeCoreEntity findEmployeeByAccountEmail(String accountEmail) {
		return find("accountEmail", accountEmail).firstResult();
	}

	public static List<EmployeeCoreEntity> findEmployeesByPage(int page, int pageSize) {
		PanacheQuery<EmployeeCoreEntity> query = EmployeeCoreEntity.findAll();
		return query.page(page, pageSize).list();
	}

	public static List<EmployeeCoreEntity> findEmployeesByJobId(UUID jobId) {
		return find("jobId", jobId).list();
	}

	public static PanacheQuery<PanacheEntityBase> findEmployeesByOrgId(UUID organizationId) {
		return find("organizationId", organizationId);
	}
}
