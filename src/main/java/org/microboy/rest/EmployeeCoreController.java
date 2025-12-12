package org.microboy.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.microboy.dto.EmployeeOverviewDTO;
import org.microboy.dto.request.EmployeeCoreRequestDTO;
import org.microboy.dto.response.EmployeeCoreResponseDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.dto.response.PaginatedResponse;
import org.microboy.service.EmployeeService;

import java.util.UUID;

import static org.microboy.security.constants.RoleConstants.*;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Employee", description = "Operations related to employees")
@RequiredArgsConstructor
public class EmployeeCoreController {

	private final EmployeeService employeeService;

	@GET
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Get all employees", description = "Returns a list of all employees")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, returns all employees",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = EmployeeCoreRequestDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findAllEmployees(@QueryParam("page") @DefaultValue("0") int page,
	                                 @QueryParam("size") @DefaultValue("20") int size) {
		PaginatedResponse<EmployeeCoreResponseDTO> employees = employeeService.findAllEmployeesByPage(page, size);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, employees))
		               .build();
	}

	@POST
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Create a new employee", description = "Return employee that is created")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, employee created",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = EmployeeCoreRequestDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response createEmployee(EmployeeCoreRequestDTO employeeRequest) {
		employeeService.createEmployee(employeeRequest);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                null))
		               .build();
	}

	@GET
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Get employee based on given id", description = "Return employee with given id")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, employee returned",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = EmployeeCoreRequestDTO.class))),
			@APIResponse(responseCode = "404", description = "Employee not found to be returned"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findById(@PathParam("id") UUID id) {
		EmployeeCoreResponseDTO employee = employeeService.findEmployeeById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                employee))
		               .build();
	}

	@GET
	@Path("/overview/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Get employee based on given id", description = "Return employee with given id")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, employee returned",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = EmployeeCoreRequestDTO.class))),
			@APIResponse(responseCode = "404", description = "Employee not found to be returned"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findEmployeeOverviewById(@PathParam("id") UUID id) {
		EmployeeCoreResponseDTO employee = employeeService.findEmployeeById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                employee))
		               .build();
	}

	@PUT
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Update employee based on given id and info", description = "Return employee updated")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, employee updated",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = EmployeeCoreRequestDTO.class))),
			@APIResponse(responseCode = "404", description = "Employee not found to be updated"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response updateEmployee(EmployeeCoreRequestDTO requestedEmployee, @PathParam("id") UUID id) {
		EmployeeCoreRequestDTO employee = employeeService.updateEmployee(requestedEmployee, id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                employee))
		               .build();
	}

	@PUT
	@Path("/overview/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Update employee overview based on given id and info", description = "Return employee overview updated")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, employee overview updated",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = EmployeeCoreRequestDTO.class))),
			@APIResponse(responseCode = "404", description = "Employee not found to be updated"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response updateEmployeeOverview(EmployeeOverviewDTO requestedEmployee, @PathParam("id") UUID id) {
		EmployeeOverviewDTO employee = employeeService.updateEmployeeOverview(requestedEmployee, id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                employee))
		               .build();
	}

	@DELETE
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Delete employee based on given id", description = "Return status ok")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, employee deleted",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = EmployeeCoreRequestDTO.class))),
			@APIResponse(responseCode = "404", description = "Employee not found to be deleted"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response deleteById(@PathParam("id") UUID id) {
		employeeService.deleteEmployeeById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
		               .build();
	}

	@GET
	@Path("/employee")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Get employee based on given account email",
	           description = "Return employee with given account email")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, employee returned",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = EmployeeCoreRequestDTO.class))),
			@APIResponse(responseCode = "404", description = "Employee not found to be returned"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findByAccountEmail(@QueryParam("accountEmail") String email) {
		EmployeeCoreResponseDTO employee = employeeService.findEmployeeByAccountEmail(email);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                employee))
		               .build();
	}
}
