package org.microboy.entity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hibernate.envers.RevisionListener;

/**
 * @author Khanh Tran
 */

@ApplicationScoped
public class CustomEntityListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		AuditRevisionEntity auditRevision = (AuditRevisionEntity) revisionEntity;
		Instance<JsonWebToken> instance = CDI.current().select(JsonWebToken.class);

		if (instance.isResolvable()) {
			JsonWebToken jwt = instance.get();
			auditRevision.setModifiedBy(jwt.getName());
		} else {
			// Handle case where JWT is not available
			auditRevision.setModifiedBy("unknown");
		}
	}
}
