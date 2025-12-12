package org.microboy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "cetus_core_revinfo")
@RevisionEntity(CustomEntityListener.class)
@Getter
@Setter
public class AuditRevisionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@RevisionNumber
	@Column(name = "rev")
	private Long rev;

	@RevisionTimestamp
	@Column(name = "timestamp")
	private long timestamp;

	@Column(name = "modified_by")
	private String modifiedBy;

}
