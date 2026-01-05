package org.microboy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestCreateDTO {

	@NotNull(message = "From date is required")
	private LocalDate fromDate;

	@NotNull(message = "To date is required")
	private LocalDate toDate;

	@NotBlank(message = "Reason is required")
	private String reason;
}
