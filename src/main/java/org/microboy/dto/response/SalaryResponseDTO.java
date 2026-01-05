package org.microboy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microboy.enums.SalaryStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryResponseDTO {

	private UUID salaryId;
	private UUID employeeId;
	private String employeeName;
	private Integer month;
	private Integer year;
	private BigDecimal basicSalary;
	private BigDecimal bonus;
	private BigDecimal deductions;
	private BigDecimal totalSalary;
	private SalaryStatus status;
	private LocalDateTime paidAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
