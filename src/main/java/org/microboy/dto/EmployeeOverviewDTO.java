package org.microboy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.microboy.enums.EmployeeStatus;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class EmployeeOverviewDTO {
	private UUID employeeId;
	private String accountEmail;
	private String companyEmail;
	private String companyPhoneNumber;
	private String employeeCode;
	private EmployeeStatus employeeStatus;
	private TeamDTO team;
	private DepartmentDTO department;
	private JobTitleDTO jobTitle;
}
