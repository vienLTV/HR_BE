package org.microboy.entity;

import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee_profile")
public class EmployeeProfileEntity extends PanacheEntityBase {

	@Id
	@Column(name = "employee_id")
	public UUID employeeId;

	@Column(name = "avatar_image")
	@Lob
	public byte[] avatarImage;

	@Column(name = "avatar_file_name")
	public String avatarFileName;

	@Column(name = "avatar_content_type")
	public String avatarContentType;

	@Column(name = "avatar_file_size")
	public Long avatarFileSize;
}
