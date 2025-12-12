package org.microboy.dto;

import java.time.LocalDate;
import java.util.UUID;

import org.microboy.enums.MaritalStatus;
import org.microboy.enums.Title;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class PersonalDetailDTO {
	private UUID employeeId;
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
}
