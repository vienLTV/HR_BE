package org.microboy.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
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
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.service.DepartmentService;

import java.util.List;
import java.util.UUID;

import static org.microboy.security.constants.RoleConstants.ADMIN;
import static org.microboy.security.constants.RoleConstants.MANAGER;
import static org.microboy.security.constants.RoleConstants.OWNER;
import static org.microboy.security.constants.RoleConstants.USER;

@Path(("/departments"))
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Department", description = "Operations related to departments")
@RequiredArgsConstructor
public class DepartmentController {

	private final DepartmentService departmentService;

	@GET
	@RolesAllowed({OWNER, ADMIN, MANAGER, USER})
	@Operation(summary = "Get all departments", description = "Returns a list of all departments")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, returns all departments",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = DepartmentDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findAllDepartments() {
		List<DepartmentDTO> departments = departmentService.findAllDepartment();
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, departments))
		               .build();
	}

	@POST
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Create a new department", description = "Return department that is created")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, department created",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = DepartmentDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response createDepartment(DepartmentDTO departmentDTO) {
		DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                createdDepartment))
		               .build();
	}

	@GET
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER, USER})
	@Operation(summary = "Get department based on given id", description = "Return department with given id")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, department returned",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = DepartmentDTO.class))),
			@APIResponse(responseCode = "404", description = "Department not found from database"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findById(@PathParam("id") UUID id) {
		DepartmentDTO department = departmentService.findDepartmentById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                department))
		               .build();
	}


	@PUT
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Update department based on given id and info", description = "Return department updated")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, department updated",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = DepartmentDTO.class))),
			@APIResponse(responseCode = "404", description = "Department not found to be updated"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response update(DepartmentDTO departmentDTO, @PathParam("id") UUID id) {
		departmentDTO = departmentService.updateDepartment(departmentDTO, id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                departmentDTO))
		               .build();
	}

	@DELETE
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Delete department based on given id", description = "Return status ok")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, department deleted",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = DepartmentDTO.class))),
			@APIResponse(responseCode = "404", description = "Department not found to be deleted"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response deleteById(@PathParam("id") UUID id) {
		departmentService.deleteDepartmentById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
		               .build();
	}
}
