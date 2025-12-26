package org.microboy.rest;

import jakarta.annotation.security.RolesAllowed;
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
@RequiredArgsConstructor
public class AttendanceController {

	private final AttendanceService attendanceService;
	private final EmployeeService employeeService;
	private final UserRepository userRepository;
	private final JsonWebToken jwt;

	@POST
	@Path("/check-in")
	@RolesAllowed({USER})
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
		UUID employeeId = getCurrentEmployeeId();
		AttendanceResponseDTO attendance = attendanceService.checkIn(employeeId, request);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                attendance))
		               .build();
	}

	@POST
	@Path("/check-out")
	@RolesAllowed({USER})
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
		UUID employeeId = getCurrentEmployeeId();
		AttendanceResponseDTO attendance = attendanceService.checkOut(employeeId, request);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                attendance))
		               .build();
	}

	@GET
	@Path("/my-attendance")
	@RolesAllowed({USER})
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
		UUID employeeId = getCurrentEmployeeId();
		PaginatedResponse<AttendanceResponseDTO> attendance = attendanceService.getMyAttendance(employeeId, page, size);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                attendance))
		               .build();
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
		AttendanceDashboardSummaryDTO summary = attendanceService.getDashboardSummary();
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                summary))
		               .build();
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
		String accountEmail = jwt.getSubject();
		if (accountEmail == null || accountEmail.trim().isEmpty()) {
			throw new jakarta.ws.rs.BadRequestException("Unable to identify logged-in user");
		}

		UserEntity userEntity = userRepository.findById(accountEmail);
		if (userEntity == null) {
			throw new jakarta.ws.rs.BadRequestException("User not found");
		}

		// Service layer handles auto-creation if needed (DEV/TEST only)
		return employeeService.ensureEmployeeExistsForUser(userEntity);
	}
}

