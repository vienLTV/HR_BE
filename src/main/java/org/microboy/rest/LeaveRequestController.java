package org.microboy.rest;

import jakarta.annotation.security.RolesAllowed;
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
import org.microboy.dto.request.LeaveRequestCreateDTO;
import org.microboy.dto.request.LeaveRequestStatusUpdateDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.dto.response.LeaveRequestResponseDTO;
import org.microboy.service.LeaveRequestService;

import java.util.List;
import java.util.UUID;

import static org.microboy.security.constants.RoleConstants.*;

@Path("/leave-requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Leave Requests", description = "Leave request management operations")
@Slf4j
@RequiredArgsConstructor
public class LeaveRequestController {

	private final LeaveRequestService leaveRequestService;
	private final JsonWebToken jwt;

	@POST
	@RolesAllowed({USER, MANAGER, ADMIN, OWNER})
	@Operation(summary = "Create leave request", description = "Employee creates a new leave request")
	@APIResponses({
		@APIResponse(responseCode = "201", description = "Leave request created successfully",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = LeaveRequestResponseDTO.class))),
		@APIResponse(responseCode = "400", description = "Bad request - validation failed"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response createLeaveRequest(@Valid LeaveRequestCreateDTO dto) {
		try {
			UUID employeeId = getCurrentEmployeeId();
			UUID organizationId = getCurrentOrganizationId();
			
			LeaveRequestResponseDTO response = leaveRequestService.createLeaveRequest(employeeId, organizationId, dto);
			
			return Response.status(Response.Status.CREATED)
				.entity(new GeneralResponseDTO<>(true, Response.Status.CREATED.getStatusCode(), null, response))
				.build();
		} catch (BadRequestException e) {
			log.warn("Leave request creation validation failed: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(new GeneralResponseDTO<>(false, Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage(), null))
				.build();
		} catch (Exception e) {
			log.error("Error creating leave request", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to create leave request", null))
				.build();
		}
	}

	@GET
	@Path("/my")
	@RolesAllowed({USER, MANAGER, ADMIN, OWNER})
	@Operation(summary = "Get my leave requests", description = "Get all leave requests for the logged-in employee")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Successfully retrieved leave requests"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getMyLeaveRequests() {
		try {
			UUID employeeId = getCurrentEmployeeId();
			UUID organizationId = getCurrentOrganizationId();
			
			List<LeaveRequestResponseDTO> requests = leaveRequestService.getMyLeaveRequests(employeeId, organizationId);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, requests))
				.build();
		} catch (Exception e) {
			log.error("Error fetching leave requests", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to fetch leave requests", null))
				.build();
		}
	}

	@GET
	@RolesAllowed({MANAGER, ADMIN, OWNER})
	@Operation(summary = "Get all leave requests", description = "Get all leave requests for the organization (managers only)")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Successfully retrieved leave requests"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getAllLeaveRequests() {
		try {
			UUID organizationId = getCurrentOrganizationId();
			
			List<LeaveRequestResponseDTO> requests = leaveRequestService.getAllLeaveRequests(organizationId);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, requests))
				.build();
		} catch (Exception e) {
			log.error("Error fetching all leave requests", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to fetch leave requests", null))
				.build();
		}
	}

	@PUT
	@Path("/{id}/status")
	@RolesAllowed({MANAGER, ADMIN, OWNER})
	@Operation(summary = "Update leave request status", description = "Approve or reject a leave request (managers only)")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Status updated successfully"),
		@APIResponse(responseCode = "400", description = "Bad request - invalid status update"),
		@APIResponse(responseCode = "404", description = "Leave request not found"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response updateLeaveRequestStatus(@PathParam("id") UUID leaveRequestId, @Valid LeaveRequestStatusUpdateDTO dto) {
		try {
			UUID approverId = getCurrentEmployeeId();
			
			LeaveRequestResponseDTO response = leaveRequestService.updateLeaveRequestStatus(leaveRequestId, approverId, dto);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, response))
				.build();
		} catch (jakarta.persistence.EntityNotFoundException e) {
			log.warn("Leave request not found: {}", leaveRequestId);
			return Response.status(Response.Status.NOT_FOUND)
				.entity(new GeneralResponseDTO<>(false, Response.Status.NOT_FOUND.getStatusCode(), e.getMessage(), null))
				.build();
		} catch (BadRequestException e) {
			log.warn("Status update validation failed: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(new GeneralResponseDTO<>(false, Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage(), null))
				.build();
		} catch (Exception e) {
			log.error("Error updating leave request status", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to update leave request status", null))
				.build();
		}
	}

	@GET
	@Path("/{id}")
	@RolesAllowed({USER, MANAGER, ADMIN, OWNER})
	@Operation(summary = "Get leave request by ID", description = "Get details of a specific leave request")
	@APIResponses({
		@APIResponse(responseCode = "200", description = "Successfully retrieved leave request"),
		@APIResponse(responseCode = "404", description = "Leave request not found"),
		@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getLeaveRequestById(@PathParam("id") UUID leaveRequestId) {
		try {
			LeaveRequestResponseDTO response = leaveRequestService.getLeaveRequestById(leaveRequestId);
			
			return Response.ok()
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, response))
				.build();
		} catch (jakarta.persistence.EntityNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND)
				.entity(new GeneralResponseDTO<>(false, Response.Status.NOT_FOUND.getStatusCode(), e.getMessage(), null))
				.build();
		} catch (Exception e) {
			log.error("Error fetching leave request", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new GeneralResponseDTO<>(false, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), 
					"Failed to fetch leave request", null))
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
