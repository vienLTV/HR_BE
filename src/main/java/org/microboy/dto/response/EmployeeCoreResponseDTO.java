package org.microboy.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import org.microboy.dto.DepartmentDTO;
import org.microboy.dto.JobTitleDTO;
import org.microboy.dto.TeamDTO;
import org.microboy.enums.EmployeeStatus;
import org.microboy.enums.Gender;
import org.microboy.enums.MaritalStatus;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class EmployeeCoreResponseDTO {
	private UUID employeeId;
	private String companyEmail;
	private String companyPhoneNumber;
	private String employeeCode;
	private EmployeeStatus employeeStatus;
	private TeamDTO team;
	private DepartmentDTO department;
	private JobTitleDTO jobTitle;
	private String firstName;
	private String lastName;
	private String preferredName;
	private String personalEmail;
	private String personalPhoneNumber;
	private LocalDate dateOfBirth;
	private String currentAddress;
	private String birthPlace;
	private MaritalStatus maritalStatus;
	private Gender gender;
}
