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
import org.microboy.dto.EmergencyContactDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.security.utils.OwnerAdminManagerAllowed;
import org.microboy.service.EmergencyContactService;

import java.util.List;
import java.util.UUID;

@Path("/emergency-contacts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Emergency Contact", description = "Operations related to emergency contact of employee")
@RequiredArgsConstructor
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;

    @GET
    @OwnerAdminManagerAllowed
    @Operation(summary = "Get all emergency contacts", description = "Returns a list of all emergency contacts")
    @APIResponse(responseCode = "200",
            description = "Successful, returns all emergency contacts",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmergencyContactDTO.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response findAllEmergencyContacts(@QueryParam("employeeId") UUID employeeId,
                                             @QueryParam("page") @DefaultValue("0") int page,
                                             @QueryParam("size") @DefaultValue("20") int pageSize) {
        List<EmergencyContactDTO> emergencyContacts = emergencyContactService.findAllEmergencyContactByEmployeeId(employeeId);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, emergencyContacts))
                .build();
    }

    @POST
    @OwnerAdminManagerAllowed
    @Operation(summary = "Create a new emergency contact", description = "Return emergency contact that is created")
    @APIResponse(responseCode = "200",
            description = "Successful, emergency contact created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmergencyContactDTO.class)))
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response createEmergencyContact(EmergencyContactDTO emergencyContactDTO) {
        EmergencyContactDTO createdEmergencyContact = emergencyContactService.createEmergencyContact(emergencyContactDTO);
        return Response.status(Response.Status.CREATED)
                .entity(new GeneralResponseDTO<>(true,
                        Response.Status.CREATED.getStatusCode(),
                        null,
                        createdEmergencyContact))
                .build();
    }

    @GET
    @Path("/{id}")
    @OwnerAdminManagerAllowed
    @Operation(summary = "Get emergency contact based on given id", description = "Return emergency contact with given id")
    @APIResponse(responseCode = "200",
            description = "Successful, emergency contact returned",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmergencyContactDTO.class)))
    @APIResponse(responseCode = "404", description = "Emergency contact not found to be returned")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response findEmergencyContactById(@PathParam("id") UUID id) {
        EmergencyContactDTO emergencyContactDTO = emergencyContactService.findEmergencyContactById(id);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true,
                        Response.Status.OK.getStatusCode(),
                        null,
                        emergencyContactDTO))
                .build();
    }

    @PUT
    @Path("/{id}")
    @OwnerAdminManagerAllowed
    @Operation(summary = "Update emergency contact based on given id and info", description = "Return emergency contact updated")
    @APIResponse(responseCode = "200",
            description = "Successful, emergency contact updated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmergencyContactDTO.class)))
    @APIResponse(responseCode = "404", description = "Emergency contact not found to be updated")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response updateEmergencyContact(EmergencyContactDTO emergencyContactDTO, @PathParam("id") UUID id) {
        EmergencyContactDTO emergencyContact = emergencyContactService.updateEmergencyContact(emergencyContactDTO, id);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true,
                        Response.Status.OK.getStatusCode(),
                        null,
                        emergencyContact))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @OwnerAdminManagerAllowed
    @Operation(summary = "Delete emergency contact based on given id", description = "Return status ok")
    @APIResponse(responseCode = "200",
            description = "Successful, emergency contact deleted",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EmergencyContactDTO.class)))
    @APIResponse(responseCode = "404", description = "Emergency contact not found to be deleted")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response deleteById(@PathParam("id") UUID id) {
        emergencyContactService.deleteEmergencyContactById(id);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
                .build();
    }
}
