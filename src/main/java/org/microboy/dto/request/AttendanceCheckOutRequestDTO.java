package org.microboy.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AttendanceCheckOutRequestDTO {
	private UUID employeeId;  // Optional - defaults to logged-in user's employeeId
	private LocalDateTime checkOutTime;  // Optional - defaults to current timestamp
	private String notes;  // Optional
}

