package org.microboy.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.microboy.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class AttendanceResponseDTO {
	private UUID attendanceId;
	private UUID organizationId;
	private UUID employeeId;
	private LocalDate attendanceDate;
	private LocalDateTime checkInTime;
	private LocalDateTime checkOutTime;
	private AttendanceStatus status;
	private String notes;
}

