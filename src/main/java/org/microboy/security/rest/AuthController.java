package org.microboy.security.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.microboy.dto.request.SignUpRequestDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.security.dto.AuthRequest;
import org.microboy.security.dto.AuthResponse;
import org.microboy.security.dto.request.ChangePasswordRequestDTO;
import org.microboy.security.dto.request.CreateEmployeeAccountRequestDTO;
import org.microboy.security.dto.UserDTO;
import org.microboy.security.enums.Role;
import org.microboy.security.service.UserService;
import org.microboy.service.SignUpService;

import java.util.UUID;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Operations related to security")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;
	private final SignUpService signUpService;

	@Inject
	JsonWebToken jwt;

	@PermitAll
	@POST
	@Path("/login")
	@Operation(summary = "Login into system", description = "Return jwt token")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, returns jwt token",
			             content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthRequest.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response login(AuthRequest authRequest) {
		AuthResponse authResponse = userService.authenticateUser(authRequest);
		if (authResponse != null) {
			return Response.status(Response.Status.OK)
			               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, authResponse))
			               .build();
		}
		return Response.status(Response.Status.UNAUTHORIZED)
		               .entity(new GeneralResponseDTO<>(true,
		                                        Response.Status.UNAUTHORIZED.getStatusCode(),
		                                        "User information is not valid",
		                                                null))
		               .build();
	}

	@PermitAll
	@POST
	@Path("/register")
	@Operation(summary = "Register admin account", description = "Return created account")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, returns created account",
			             content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthRequest.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response register(UserDTO userDTO) {
		UserDTO createdUser = userService.createUser(userDTO);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                createdUser))
		               .build();
	}

	@PermitAll
	@POST
	@Path("/sign-up")
	@Operation(summary = "Register admin account", description = "Return created account")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, returns created account",
			             content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthRequest.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response createOrgAndUser(SignUpRequestDTO signUpRequestDTO) {
		signUpService.createOrgAndOwnerAccount(signUpRequestDTO);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                null))
		               .build();
	}

	@RolesAllowed({"OWNER", "ADMIN"})
	@POST
	@Path("/create-employee-account")
	@Operation(summary = "Create user account for existing employee")
	@APIResponses({
			@APIResponse(responseCode = "201",
			             description = "User account created",
			             content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
			@APIResponse(responseCode = "400", description = "Invalid request"),
			@APIResponse(responseCode = "409", description = "Duplicate account")
	})
	public Response createEmployeeAccount(CreateEmployeeAccountRequestDTO request) {
		var createdUser = userService.createUserForEmployee(request);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                createdUser))
		               .build();
	}

	@RolesAllowed({"USER", "MANAGER", "ADMIN", "OWNER"})
	@PUT
	@Path("/change-password")
	@Operation(summary = "Change password", description = "Change user password")
	@APIResponses({
			@APIResponse(responseCode = "200", description = "Password changed successfully"),
			@APIResponse(responseCode = "400", description = "Invalid request"),
			@APIResponse(responseCode = "401", description = "Unauthorized")
	})
	public Response changePassword(ChangePasswordRequestDTO request) {
		UUID employeeId = UUID.fromString(jwt.getClaim("employeeId"));
		userService.changePassword(employeeId, request);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                "Password changed successfully",
		                                                null))
		               .build();
	}

}
