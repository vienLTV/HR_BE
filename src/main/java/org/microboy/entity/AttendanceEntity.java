package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.microboy.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attendance")
public class AttendanceEntity extends PanacheEntityBase {

	@Id
	@UuidGenerator
	@Column(name = "attendance_id")
	public UUID attendanceId;

	@Column(name = "organization_id")
	public UUID organizationId;

	@Column(name = "employee_id")
	public UUID employeeId;

	@Column(name = "attendance_date")
	public LocalDate attendanceDate;

	@Column(name = "check_in_time")
	public LocalDateTime checkInTime;

	@Column(name = "check_out_time")
	public LocalDateTime checkOutTime;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	public AttendanceStatus status;

	@Column(name = "notes")
	public String notes;

	public static AttendanceEntity findByOrgAndEmployeeAndDate(UUID organizationId, UUID employeeId, LocalDate date) {
		return find("organizationId = ?1 AND employeeId = ?2 AND attendanceDate = ?3", organizationId, employeeId, date)
			.firstResult();
	}

	public static PanacheQuery<AttendanceEntity> findByOrgAndEmployee(UUID organizationId, UUID employeeId) {
		return find("organizationId = ?1 AND employeeId = ?2", organizationId, employeeId);
	}

	public static List<AttendanceEntity> findByOrgAndEmployeeAndDateRange(UUID organizationId, UUID employeeId,
	                                                                      LocalDate startDate, LocalDate endDate) {
		return find("organizationId = ?1 AND employeeId = ?2 AND attendanceDate >= ?3 AND attendanceDate <= ?4",
		            organizationId, employeeId, startDate, endDate).list();
	}

	public static PanacheQuery<AttendanceEntity> findByOrganization(UUID organizationId) {
		return find("organizationId", organizationId);
	}
}

