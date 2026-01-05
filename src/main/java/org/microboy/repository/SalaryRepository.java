package org.microboy.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.microboy.entity.SalaryEntity;
import org.microboy.enums.SalaryStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SalaryRepository implements PanacheRepositoryBase<SalaryEntity, UUID> {

	/**
	 * Find all salary records for a specific employee
	 */
	public List<SalaryEntity> findByEmployeeId(UUID employeeId) {
		return list("employeeId = ?1 ORDER BY year DESC, month DESC", employeeId);
	}

	/**
	 * Find all salary records for an organization
	 */
	public List<SalaryEntity> findByOrganizationId(UUID organizationId) {
		return list("organizationId = ?1 ORDER BY year DESC, month DESC", organizationId);
	}

	/**
	 * Find salary record for specific employee, month, and year
	 */
	public Optional<SalaryEntity> findByEmployeeAndPeriod(UUID employeeId, Integer month, Integer year) {
		return find("employeeId = ?1 AND month = ?2 AND year = ?3", employeeId, month, year).firstResultOptional();
	}

	/**
	 * Find all salary records for a specific month and year in an organization
	 */
	public List<SalaryEntity> findByOrganizationAndPeriod(UUID organizationId, Integer month, Integer year) {
		return list("organizationId = ?1 AND month = ?2 AND year = ?3", organizationId, month, year);
	}

	/**
	 * Find salary records by status
	 */
	public List<SalaryEntity> findByOrganizationAndStatus(UUID organizationId, SalaryStatus status) {
		return list("organizationId = ?1 AND status = ?2 ORDER BY year DESC, month DESC", organizationId, status);
	}

	/**
	 * Count salary records by organization and status
	 */
	public long countByOrganizationAndStatus(UUID organizationId, SalaryStatus status) {
		return count("organizationId = ?1 AND status = ?2", organizationId, status);
	}

	/**
	 * Find salary records for employees in a specific team
	 * Note: This requires joining with employee_core table
	 */
	public List<SalaryEntity> findByTeamMembers(List<UUID> employeeIds) {
		if (employeeIds == null || employeeIds.isEmpty()) {
			return List.of();
		}
		return list("employeeId IN ?1 ORDER BY year DESC, month DESC", employeeIds);
	}
}
