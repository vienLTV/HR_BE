package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee_job_title")
public class EmployeeJobTitleEntity extends PanacheEntityBase {

	@Id
	@Column(name = "employee_id")
	public UUID employeeId;

	@Column(name = "job_id")
	public UUID jobId;

}
