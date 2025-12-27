package org.microboy.security.config;

import jakarta.annotation.Priority;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
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
@Priority(Priorities.AUTHENTICATION)
public class JWTValidationFilter implements ContainerRequestFilter {

	@Inject
	OrganizationContext organizationContext;

	@Inject
	ResourceInfo resourceInfo;

	@Inject
	JsonWebToken jwt;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// 1️⃣ Bỏ qua filter cho @PermitAll
		Method method = resourceInfo.getResourceMethod();
		if (method != null && method.isAnnotationPresent(PermitAll.class)) {
			return;
		}

		// 2️⃣ Bỏ qua các endpoint /auth/** để không chặn login/register
		String path = requestContext.getUriInfo().getPath();
		if (path != null && path.startsWith("auth")) {
			return;
		}

		// 3️⃣ Lấy Authorization header tối thiểu để phát hiện thiếu JWT sớm
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			requestContext.abortWith(
				Response.status(Response.Status.UNAUTHORIZED)
				        .entity("Missing or invalid Authorization header")
				        .build()
			);
			return;
		}

		// 4️⃣ Dùng JsonWebToken đã được Quarkus xác thực (không tự parse thủ công)
		if (jwt == null || jwt.getClaimNames() == null) {
			requestContext.abortWith(
				Response.status(Response.Status.UNAUTHORIZED)
				        .entity("JWT principal not available")
				        .build()
			);
			return;
		}

		// 5️⃣ Lấy organizationId từ claim
		Object orgIdClaim = jwt.getClaim("organizationId");
		if (orgIdClaim == null) {
			requestContext.abortWith(
				Response.status(Response.Status.UNAUTHORIZED)
				        .entity("organizationId not found in JWT")
				        .build()
			);
			return;
		}

		try {
			UUID organizationId = UUID.fromString(orgIdClaim.toString());

			if (organizationContext == null) {
				requestContext.abortWith(
					Response.status(Response.Status.UNAUTHORIZED)
					        .entity("OrganizationContext not initialized")
					        .build()
				);
				return;
			}

			// 6️⃣ Set vào OrganizationContext (được dùng bởi service layer)
			organizationContext.setCurrentOrganizationId(organizationId);

			// Log nhẹ để theo dõi
			String subject = jwt.getSubject();
			System.out.println("✅ JWT validated - organizationId=" + organizationId + " user=" + subject);

		} catch (IllegalArgumentException e) {
			requestContext.abortWith(
				Response.status(Response.Status.UNAUTHORIZED)
				        .entity("Invalid organizationId in JWT")
				        .build()
			);
		}
	}
}
