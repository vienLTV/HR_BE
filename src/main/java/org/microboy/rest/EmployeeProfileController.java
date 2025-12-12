package org.microboy.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.microboy.dto.DepartmentDTO;
import org.microboy.dto.EmployeeProfileDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.service.EmployeeProfileService;

import java.util.UUID;

import static org.microboy.security.constants.RoleConstants.ADMIN;
import static org.microboy.security.constants.RoleConstants.MANAGER;
import static org.microboy.security.constants.RoleConstants.OWNER;

@Path("/employees/profile")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Employee Profile", description = "Operations related to employees profile")
@RequiredArgsConstructor
public class EmployeeProfileController {

	private final EmployeeProfileService employeeProfileService;

	@GET
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Get profile based on given id", description = "Return employee profile with given id")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, profile returned",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = DepartmentDTO.class))),
			@APIResponse(responseCode = "404", description = "profile not found from database"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findById(@PathParam("id") UUID id) {
		EmployeeProfileDTO employeeProfileDTO = employeeProfileService.getEmployeeProfileById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                employeeProfileDTO))
		               .build();
	}

	@POST
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Update employee profile", description = "Return info that is updated")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, profiled created",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = EmployeeProfileDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response updateEmployeeAvatar(EmployeeProfileDTO employeeProfileDTO) {
		employeeProfileService.updateEmployeeAvatar(employeeProfileDTO);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
		               .build();
	}
}
