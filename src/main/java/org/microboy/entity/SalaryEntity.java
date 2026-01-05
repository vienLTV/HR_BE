package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microboy.enums.SalaryStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "salary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class SalaryEntity extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "salary_id")
	public UUID salaryId;

	@Column(name = "employee_id", nullable = false)
	public UUID employeeId;

	@Column(name = "organization_id", nullable = false)
	public UUID organizationId;

	@Column(name = "month", nullable = false)
	public Integer month;

	@Column(name = "year", nullable = false)
	public Integer year;

	@Column(name = "basic_salary", nullable = false, precision = 15, scale = 2)
	public BigDecimal basicSalary;

	@Column(name = "bonus", precision = 15, scale = 2)
	public BigDecimal bonus = BigDecimal.ZERO;

	@Column(name = "deductions", precision = 15, scale = 2)
	public BigDecimal deductions = BigDecimal.ZERO;

	@Column(name = "total_salary", nullable = false, precision = 15, scale = 2)
	public BigDecimal totalSalary;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	public SalaryStatus status = SalaryStatus.PENDING;

	@Column(name = "paid_at")
	public LocalDateTime paidAt;

	@Column(name = "created_at", nullable = false)
	public LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at")
	public LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		calculateTotalSalary();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
		calculateTotalSalary();
	}

	private void calculateTotalSalary() {
		if (basicSalary != null) {
			BigDecimal bonusAmount = (bonus != null) ? bonus : BigDecimal.ZERO;
			BigDecimal deductionAmount = (deductions != null) ? deductions : BigDecimal.ZERO;
			totalSalary = basicSalary.add(bonusAmount).subtract(deductionAmount);
		}
	}
}
