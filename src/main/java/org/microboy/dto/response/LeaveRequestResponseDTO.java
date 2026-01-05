package org.microboy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microboy.enums.LeaveStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResponseDTO {

	private UUID leaveRequestId;
	private UUID employeeId;
	private String employeeName;
	private LocalDate fromDate;
	private LocalDate toDate;
	private String reason;
	private LeaveStatus status;
	private UUID approvedBy;
	private String approvedByName;
	private LocalDateTime approvedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
