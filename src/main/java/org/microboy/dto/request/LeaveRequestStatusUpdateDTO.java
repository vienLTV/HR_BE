package org.microboy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microboy.enums.LeaveStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestStatusUpdateDTO {

	@NotNull(message = "Status is required")
	private LeaveStatus status;
}
