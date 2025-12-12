package org.microboy.security.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.microboy.security.entity.UserRoleEntity;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRoleRepository implements PanacheRepositoryBase<UserRoleEntity, UUID> {

	public List<UserRoleEntity> findUserRoleByAccountEmail(String accountEmail) {
		return find("accountEmail", accountEmail).stream().toList();
	}
}
