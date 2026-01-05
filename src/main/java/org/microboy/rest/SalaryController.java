package org.microboy.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.microboy.dto.request.CalculateSalaryRequestDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.dto.response.SalaryResponseDTO;
import org.microboy.service.SalaryService;

import java.util.List;
import java.util.UUID;

@Path("/salary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Salary", description = "Salary management operations")
@Slf4j
@RequiredArgsConstructor
public class SalaryController {

	private final SalaryService salaryService;
	private final JsonWebToken jwt;

	@GET
	@Path("/my-salary")
	@RolesAllowed({"USER", "MANAGER", "ADMIN", "OWNER"})
	@Operation(summary = "Get my salary", description = "Get salary records for the logged-in employee")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Successfully retrieved salary records"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getMySalary() {
		try {
			UUID employeeId = getCurrentEmployeeId();
			UUID organizationId = getCurrentOrganizationId();
			
			List<SalaryResponseDTO> salaries = salaryService.getMySalary(employeeId, organizationId);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, salaries))
				.build();
		} catch (Exception e) {
			log.error("Error fetching my salary", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to fetch salary records", null))
				.build();
		}
	}

	@GET
	@Path("/team-salary")
	@RolesAllowed({"MANAGER", "ADMIN", "OWNER"})
	@Operation(summary = "Get team salary", description = "Get salary records for team members (managers only)")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Successfully retrieved team salary records"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getTeamSalary() {
		try {
			UUID managerId = getCurrentEmployeeId();
			UUID organizationId = getCurrentOrganizationId();
			
			List<SalaryResponseDTO> salaries = salaryService.getTeamSalary(managerId, organizationId);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, salaries))
				.build();
		} catch (Exception e) {
			log.error("Error fetching team salary", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to fetch team salary records", null))
				.build();
		}
	}

	@GET
	@Path("/all")
	@RolesAllowed({"ADMIN", "OWNER"})
	@Operation(summary = "Get all salary records", description = "Get all salary records in organization (admins only)")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Successfully retrieved all salary records"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getAllSalary() {
		try {
			UUID organizationId = getCurrentOrganizationId();
			
			List<SalaryResponseDTO> salaries = salaryService.getAllSalary(organizationId);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, salaries))
				.build();
		} catch (Exception e) {
			log.error("Error fetching all salary", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to fetch salary records", null))
				.build();
		}
	}

	@POST
	@Path("/calculate")
	@RolesAllowed({"ADMIN", "OWNER"})
	@Operation(summary = "Calculate salary", description = "Calculate salary for all employees for a specific period")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Salary calculated successfully",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalaryResponseDTO.class))),
		@APIResponse(responseCode = "400", description = "Bad request - validation failed"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response calculateSalary(@Valid CalculateSalaryRequestDTO dto) {
		try {
			UUID organizationId = getCurrentOrganizationId();
			
			List<SalaryResponseDTO> salaries = salaryService.calculateSalary(organizationId, dto);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), 
					"Salary calculated successfully", salaries))
				.build();
		} catch (BadRequestException e) {
			log.warn("Salary calculation validation failed: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(new GeneralResponseDTO<>(false, Response.Status.BAD_REQUEST.getStatusCode(), 
					e.getMessage(), null))
				.build();
		} catch (Exception e) {
			log.error("Error calculating salary", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to calculate salary", null))
				.build();
		}
	}

	@PUT
	@Path("/{id}/mark-paid")
	@RolesAllowed({"ADMIN", "OWNER"})
	@Operation(summary = "Mark salary as paid", description = "Mark a salary record as paid")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Salary marked as paid successfully"),
		@APIResponse(responseCode = "400", description = "Bad request"),
		@APIResponse(responseCode = "404", description = "Salary record not found"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response markAsPaid(@PathParam("id") UUID salaryId) {
		try {
			UUID organizationId = getCurrentOrganizationId();
			
			SalaryResponseDTO salary = salaryService.markAsPaid(salaryId, organizationId);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), 
					"Salary marked as paid", salary))
				.build();
		} catch (BadRequestException e) {
			log.warn("Mark as paid validation failed: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(new GeneralResponseDTO<>(false, Response.Status.BAD_REQUEST.getStatusCode(), 
					e.getMessage(), null))
				.build();
		} catch (ForbiddenException e) {
			log.warn("Forbidden access to salary record: {}", e.getMessage());
			return Response.status(Response.Status.FORBIDDEN)
				.entity(new GeneralResponseDTO<>(false, Response.Status.FORBIDDEN.getStatusCode(), 
					e.getMessage(), null))
				.build();
		} catch (Exception e) {
			log.error("Error marking salary as paid", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to mark salary as paid", null))
				.build();
		}
	}

	@GET
	@Path("/{id}")
	@RolesAllowed({"USER", "MANAGER", "ADMIN", "OWNER"})
	@Operation(summary = "Get salary by ID", description = "Get details of a specific salary record")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Successfully retrieved salary record"),
		@APIResponse(responseCode = "404", description = "Salary record not found"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getSalaryById(@PathParam("id") UUID salaryId) {
		try {
			SalaryResponseDTO salary = salaryService.getSalaryById(salaryId);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, salary))
				.build();
		} catch (Exception e) {
			log.error("Error fetching salary by ID", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to fetch salary record", null))
				.build();
		}
	}

	private UUID getCurrentEmployeeId() {
		try {
			Object employeeIdClaim = jwt.getClaim("employeeId");
			if (employeeIdClaim == null) {
				throw new BadRequestException("employeeId not found in JWT token");
			}
			return UUID.fromString(employeeIdClaim.toString());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Invalid employeeId format in JWT token");
		}
	}

	private UUID getCurrentOrganizationId() {
		try {
			Object organizationIdClaim = jwt.getClaim("organizationId");
			if (organizationIdClaim == null) {
				throw new BadRequestException("organizationId not found in JWT token");
			}
			return UUID.fromString(organizationIdClaim.toString());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Invalid organizationId format in JWT token");
		}
	}
}
