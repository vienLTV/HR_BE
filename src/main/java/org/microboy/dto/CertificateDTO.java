package org.microboy.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@RegisterForReflection
public class CertificateDTO {
	private UUID certificateId;
	private String name;
	private String description;
	private String provider;
	private String level;
	private String licenseCode;
	private LocalDate issuedDate;
	private LocalDate expiredDate;
	private String certificateImg;
	private UUID employeeId;

}
