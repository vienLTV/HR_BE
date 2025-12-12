package org.microboy.security.config;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

@Provider
public class JWTValidationFilter implements ContainerRequestFilter {
	@Inject
	JWTParser jwtParser;

	@Inject
	OrganizationContext organizationContext;

	@Inject
	ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {
		Method method = resourceInfo.getResourceMethod();
		if (method.isAnnotationPresent(PermitAll.class)) {
			// Skip validation for methods annotated with @PermitAll
			return;
		}

		String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7).trim();

			try {
				JsonWebToken jsonWebToken =  jwtParser.parse(token);
				UUID organizationId = UUID.fromString(jsonWebToken.getClaim("organizationId"));
				if (organizationId != null) {
					organizationContext.setCurrentOrganizationId(organizationId);
				}
			} catch (ParseException e) {
				containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
		} else {
			containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}
}
