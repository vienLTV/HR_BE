package org.microboy.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
import org.microboy.dto.request.AttendanceCheckInRequestDTO;
import org.microboy.dto.request.AttendanceCheckOutRequestDTO;
import org.microboy.dto.response.AttendanceDashboardSummaryDTO;
import org.microboy.dto.response.AttendanceResponseDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.dto.response.PaginatedResponse;
import org.microboy.security.entity.UserEntity;
import org.microboy.security.repository.UserRepository;
import org.microboy.service.AttendanceService;
import org.microboy.service.EmployeeService;

import java.util.UUID;

import static org.microboy.security.constants.RoleConstants.ADMIN;
import static org.microboy.security.constants.RoleConstants.OWNER;
import static org.microboy.security.constants.RoleConstants.USER;

@Path("/attendance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Attendance", description = "Operations related to employee attendance")
@Slf4j
@RequiredArgsConstructor
public class AttendanceController {

	private final AttendanceService attendanceService;
	private final EmployeeService employeeService;
	private final UserRepository userRepository;
	private final JsonWebToken jwt;

	@POST
	@Path("/check-in")
	@RolesAllowed({USER, OWNER})
	@Operation(summary = "Check in for the day", description = "Employee checks in for today. Creates a new attendance record with PENDING status.")
	@APIResponses({
		@APIResponse(responseCode = "201",
		             description = "Successfully checked in",
		             content = @Content(mediaType = "application/json",
		                                schema = @Schema(implementation = AttendanceResponseDTO.class))),
		@APIResponse(responseCode = "400", description = "Bad request - already checked in today or invalid data"),
		@APIResponse(responseCode = "404", description = "Employee not found"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response checkIn(AttendanceCheckInRequestDTO request) {
		log.debug("Attendance check-in request received");
		try {
			UUID employeeId = getCurrentEmployeeId();
			UUID organizationId = getCurrentOrganizationId();
			log.info("Processing check-in for employeeId: {} orgId: {}", employeeId, organizationId);
			AttendanceResponseDTO attendance = attendanceService.checkIn(employeeId, organizationId, request);
			log.info("Check-in successful for employeeId: {}", employeeId);
			return Response.status(Response.Status.CREATED)
			               .entity(new GeneralResponseDTO<>(true,
			                                                Response.Status.CREATED.getStatusCode(),
			                                                null,
			                                                attendance))
			               .build();
		} catch (BadRequestException | EntityNotFoundException e) {
			log.warn("Check-in validation failed: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity(new GeneralResponseDTO<>(false,
			                                                Response.Status.BAD_REQUEST.getStatusCode(),
			                                                e.getMessage(),
			                                                null))
			               .build();
		} catch (Exception e) {
			log.error("Unexpected error during check-in", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			               .entity(new GeneralResponseDTO<>(false,
			                                                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
			                                                "An unexpected error occurred",
			                                                null))
			               .build();
		}
	}

	@POST
	@Path("/check-out")
	@RolesAllowed({USER, OWNER})
	@Operation(summary = "Check out for the day", description = "Employee checks out for today. Updates the attendance record and sets status to PRESENT.")
	@APIResponses({
		@APIResponse(responseCode = "200",
		             description = "Successfully checked out",
		             content = @Content(mediaType = "application/json",
		                                schema = @Schema(implementation = AttendanceResponseDTO.class))),
		@APIResponse(responseCode = "400", description = "Bad request - no check-in found or already checked out"),
		@APIResponse(responseCode = "404", description = "Employee not found"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response checkOut(AttendanceCheckOutRequestDTO request) {
		try {
			UUID employeeId = getCurrentEmployeeId();
			UUID organizationId = getCurrentOrganizationId();
			AttendanceResponseDTO attendance = attendanceService.checkOut(employeeId, organizationId, request);
			return Response.status(Response.Status.OK)
			               .entity(new GeneralResponseDTO<>(true,
			                                                Response.Status.OK.getStatusCode(),
			                                                null,
			                                                attendance))
			               .build();
		} catch (BadRequestException | EntityNotFoundException e) {
			log.warn("Check-out validation failed: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity(new GeneralResponseDTO<>(false,
			                                                Response.Status.BAD_REQUEST.getStatusCode(),
			                                                e.getMessage(),
			                                                null))
			               .build();
		} catch (Exception e) {
			log.error("Unexpected error during check-out", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			               .entity(new GeneralResponseDTO<>(false,
			                                                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
			                                                "An unexpected error occurred",
			                                                null))
			               .build();
		}
	}

	@GET
	@Path("/my-attendance")
	@RolesAllowed({USER, OWNER})
	@Operation(summary = "Get my attendance records", description = "Returns paginated list of attendance records for the logged-in employee")
	@APIResponses({
		@APIResponse(responseCode = "200",
		             description = "Successfully retrieved attendance records",
		             content = @Content(mediaType = "application/json",
		                                schema = @Schema(implementation = PaginatedResponse.class))),
		@APIResponse(responseCode = "400", description = "Bad request - invalid parameters"),
		@APIResponse(responseCode = "404", description = "Employee not found"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getMyAttendance(@QueryParam("page") @DefaultValue("0") int page,
	                                @QueryParam("size") @DefaultValue("20") int size) {
		try {
			UUID employeeId = getCurrentEmployeeId();
			UUID organizationId = getCurrentOrganizationId();
			PaginatedResponse<AttendanceResponseDTO> attendance = attendanceService.getMyAttendance(employeeId, organizationId, page, size);
			return Response.status(Response.Status.OK)
			               .entity(new GeneralResponseDTO<>(true,
			                                                Response.Status.OK.getStatusCode(),
			                                                null,
			                                                attendance))
			               .build();
		} catch (BadRequestException | EntityNotFoundException e) {
			log.warn("Get attendance validation failed: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity(new GeneralResponseDTO<>(false,
			                                                Response.Status.BAD_REQUEST.getStatusCode(),
			                                                e.getMessage(),
			                                                null))
			               .build();
		} catch (Exception e) {
			log.error("Unexpected error getting attendance", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			               .entity(new GeneralResponseDTO<>(false,
			                                                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
			                                                "An unexpected error occurred",
			                                                null))
			               .build();
		}
	}

	@GET
	@Path("/dashboard-summary")
	@RolesAllowed({OWNER, ADMIN})
	@Operation(summary = "Get attendance dashboard summary", description = "Returns summary statistics for attendance dashboard (total employees and today's attendance)")
	@APIResponses({
		@APIResponse(responseCode = "200",
		             description = "Successfully retrieved dashboard summary",
		             content = @Content(mediaType = "application/json",
		                                schema = @Schema(implementation = AttendanceDashboardSummaryDTO.class))),
		@APIResponse(responseCode = "400", description = "Bad request - organization ID not found"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getDashboardSummary() {
		try {
			UUID organizationId = getCurrentOrganizationId();
			AttendanceDashboardSummaryDTO summary = attendanceService.getDashboardSummary(organizationId);
			return Response.status(Response.Status.OK)
			               .entity(new GeneralResponseDTO<>(true,
			                                                Response.Status.OK.getStatusCode(),
			                                                null,
			                                                summary))
			               .build();
		} catch (BadRequestException e) {
			log.warn("Dashboard summary validation failed: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity(new GeneralResponseDTO<>(false,
			                                                Response.Status.BAD_REQUEST.getStatusCode(),
			                                                e.getMessage(),
			                                                null))
			               .build();
		} catch (Exception e) {
			log.error("Unexpected error getting dashboard summary", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
			               .entity(new GeneralResponseDTO<>(false,
			                                                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
			                                                "An unexpected error occurred",
			                                                null))
			               .build();
		}
	}

	/**
	 * Gets the current logged-in user's employeeId from JWT token.
	 * Extracts accountEmail from JWT subject, then looks up UserEntity and ensures
	 * an employee record exists (DEV/TEST auto-creation handled by service layer).
	 *
	 * @return UUID of the current employee
	 * @throws jakarta.ws.rs.BadRequestException if user not found
	 */
	private UUID getCurrentEmployeeId() {
		try {
			if (jwt == null) {
				log.error("JsonWebToken is null - JWT injection failed");
				throw new BadRequestException("Authentication token not available");
			}

			Object employeeIdClaim = jwt.getClaim("employeeId");
			if (employeeIdClaim == null) {
				log.error("employeeId claim not found in JWT. Available claims: {}", jwt.getClaimNames());
				throw new BadRequestException("employeeId not found in JWT token. Please ensure your account is linked to an employee.");
			}

			String employeeIdStr = employeeIdClaim.toString();
			log.debug("Extracted employeeId claim: {}", employeeIdStr);
			
			UUID employeeId = UUID.fromString(employeeIdStr);
			log.debug("Successfully parsed employeeId: {}", employeeId);
			return employeeId;
			
		} catch (IllegalArgumentException e) {
			log.error("Failed to parse employeeId from JWT claim", e);
			throw new BadRequestException("Invalid employeeId format in JWT token");
		}
	}

	private UUID getCurrentOrganizationId() {
		try {
			if (jwt == null) {
				log.error("JsonWebToken is null - JWT injection failed");
				throw new BadRequestException("Authentication token not available");
			}

			Object organizationIdClaim = jwt.getClaim("organizationId");
			if (organizationIdClaim == null) {
				log.error("organizationId claim not found in JWT. Available claims: {}", jwt.getClaimNames());
				throw new BadRequestException("organizationId not found in JWT token.");
			}

			String organizationIdStr = organizationIdClaim.toString();
			log.debug("Extracted organizationId claim: {}", organizationIdStr);

			UUID organizationId = UUID.fromString(organizationIdStr);
			log.debug("Successfully parsed organizationId: {}", organizationId);
			return organizationId;

		} catch (IllegalArgumentException e) {
			log.error("Failed to parse organizationId from JWT claim", e);
			throw new BadRequestException("Invalid organizationId format in JWT token");
		}
	}

}

