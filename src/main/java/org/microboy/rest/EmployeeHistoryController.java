package org.microboy.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.microboy.dto.request.EmployeeHistoryRequestDTO;
import org.microboy.dto.response.EmployeeHistoryResponseDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.security.utils.OwnerAdminManagerAllowed;
import org.microboy.service.EmployeeHistoryService;

import java.util.UUID;

@Path("/employee-history")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Employee History", description = "Operations related to histories of employee")
@RequiredArgsConstructor
public class EmployeeHistoryController {

    private final EmployeeHistoryService employeeHistoryService;

    @GET
    @Path("/{employeeId}")
    @OwnerAdminManagerAllowed
    @Operation(summary = "Get all employee histories", description = "Returns a list of all employee histories")
    @APIResponse(responseCode = "200",
            description = "Successful, returns all employee histories",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeHistoryResponseDTO.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")

    public Response findAllEmployeeHistories(@PathParam("employeeId") UUID employeeId) {
        var histories = employeeHistoryService.findAllEmployeeHistoryByEmployeeId(employeeId);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, histories))
                .build();
    }

    @POST
    @OwnerAdminManagerAllowed
    @Operation(summary = "Create a new employee history", description = "Return employee history that is created")
    @APIResponse(responseCode = "200",
            description = "Successful, employee created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeHistoryResponseDTO.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")

    public Response createEmployeeHistory(EmployeeHistoryRequestDTO employeeHistoryRequestDTO) {
        EmployeeHistoryResponseDTO createdEmployeeHistory = employeeHistoryService.createEmployeeHistory(employeeHistoryRequestDTO);
        return Response.status(Response.Status.CREATED)
                .entity(new GeneralResponseDTO<>(true,
                        Response.Status.CREATED.getStatusCode(),
                        null,
                        createdEmployeeHistory))
                .build();
    }

    @GET
    @Path("/{id}")
    @OwnerAdminManagerAllowed
    @Operation(summary = "Get employee history based on given id", description = "Return employee history with given id")
    @APIResponse(responseCode = "200",
            description = "Successful, employee history returned",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeHistoryResponseDTO.class)))
    @APIResponse(responseCode = "404", description = "Employee history not found to be returned")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response findEmployeeHistoryById(@PathParam("id") UUID id) {
        EmployeeHistoryResponseDTO employeeHistoryResponse = employeeHistoryService.findEmployeeHistoryById(id);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true,
                        Response.Status.OK.getStatusCode(),
                        null,
                        employeeHistoryResponse))
                .build();
    }

    @PUT
    @Path("/{id}")
    @OwnerAdminManagerAllowed
    @Operation(summary = "Update employee history based on given id and info", description = "Return employee history updated")
    @APIResponse(responseCode = "200",
            description = "Successful, employee history updated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeHistoryResponseDTO.class)))
    @APIResponse(responseCode = "404", description = "Employee history not found to be updated")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response updateEmployeeHistory(EmployeeHistoryRequestDTO employeeHistoryRequest, @PathParam("id") UUID id) {
        EmployeeHistoryResponseDTO employeeHistoryResponse = employeeHistoryService.updateEmployeeHistory(employeeHistoryRequest, id);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true,
                        Response.Status.OK.getStatusCode(),
                        null,
                        employeeHistoryResponse))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @OwnerAdminManagerAllowed
    @Operation(summary = "Delete employee history based on given id", description = "Return status ok")
    @APIResponse(responseCode = "200",
            description = "Successful, employee history deleted",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeHistoryRequestDTO.class)))
    @APIResponse(responseCode = "404", description = "Employee history not found to be deleted")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response deleteById(@PathParam("id") UUID id) {
        employeeHistoryService.deleteEmployeeHistoryById(id);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
                .build();
    }

}
