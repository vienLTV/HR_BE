package org.microboy.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.microboy.enums.EmployeeStatus;
import org.microboy.enums.Gender;
import org.microboy.enums.MaritalStatus;
import org.microboy.enums.Title;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class EmployeeCoreRequestDTO {
	private String companyEmail;
	private String companyPhoneNumber;
	private UUID jobTitleId;
	private UUID teamId;
	private String firstName;
	private String lastName;
	private String preferredName;
	private String personalEmail;
	private String personalPhoneNumber;
	private LocalDate dateOfBirth;
	private String currentAddress;
	private String birthPlace;
	private MaritalStatus maritalStatus;
	private Title title;
	private UUID organizationId;
	private EmployeeStatus employeeStatus;
	private Gender gender;
}
