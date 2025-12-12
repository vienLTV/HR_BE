package org.microboy.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Audited
@Table(name = "job_title")
public class JobTitleEntity extends PanacheEntityBase {

	@Id
	@UuidGenerator
	@Column(name = "job_title_id")
	public UUID jobTitleId;

	@Column(name = "title")
	public String title;

	@Column(name = "description")
	public String description;

}
