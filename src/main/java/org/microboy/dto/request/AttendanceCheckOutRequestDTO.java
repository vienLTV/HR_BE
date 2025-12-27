package org.microboy.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceCheckOutRequestDTO {
	private LocalDateTime checkOutTime;  // Optional - defaults to current timestamp
	private String notes;  // Optional
}

