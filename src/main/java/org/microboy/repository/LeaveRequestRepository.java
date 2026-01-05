package org.microboy.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.microboy.entity.LeaveRequestEntity;
import org.microboy.enums.LeaveStatus;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class LeaveRequestRepository implements PanacheRepositoryBase<LeaveRequestEntity, UUID> {

	public List<LeaveRequestEntity> findByEmployeeId(UUID employeeId) {
		return list("employeeId = ?1 ORDER BY createdAt DESC", employeeId);
	}

	public List<LeaveRequestEntity> findByOrganizationId(UUID organizationId) {
		return list("organizationId = ?1 ORDER BY createdAt DESC", organizationId);
	}

	public List<LeaveRequestEntity> findByStatus(UUID organizationId, LeaveStatus status) {
		return list("organizationId = ?1 AND status = ?2 ORDER BY createdAt DESC", organizationId, status);
	}

	public long countByOrganizationAndStatus(UUID organizationId, LeaveStatus status) {
		return count("organizationId = ?1 AND status = ?2", organizationId, status);
	}
}
