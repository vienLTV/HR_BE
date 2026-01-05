package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.microboy.enums.LeaveStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leave_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestEntity extends PanacheEntityBase {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "leave_request_id")
	public UUID leaveRequestId;

	@Column(name = "employee_id", nullable = false)
	public UUID employeeId;

	@Column(name = "organization_id", nullable = false)
	public UUID organizationId;

	@Column(name = "from_date", nullable = false)
	public LocalDate fromDate;

	@Column(name = "to_date", nullable = false)
	public LocalDate toDate;

	@Column(name = "reason", nullable = false, length = 1000)
	public String reason;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	public LeaveStatus status = LeaveStatus.PENDING;

	@Column(name = "approved_by")
	public UUID approvedBy;

	@Column(name = "approved_at")
	public LocalDateTime approvedAt;

	@Column(name = "created_at", nullable = false)
	public LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at")
	public LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
