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
import org.microboy.dto.TeamDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.enums.TeamRole;
import org.microboy.service.TeamService;

import java.util.List;
import java.util.UUID;

import static org.microboy.security.constants.RoleConstants.ADMIN;
import static org.microboy.security.constants.RoleConstants.MANAGER;
import static org.microboy.security.constants.RoleConstants.OWNER;
import static org.microboy.security.constants.RoleConstants.USER;

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Tag(name = "Team Controller", description = "team operations")
public class TeamController {

    private final TeamService teamService;

    @POST
    @RolesAllowed({OWNER, ADMIN, MANAGER})
    @Operation(summary = "Create a new team", description = "Return team that is created")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Successful, team created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response createTeam(TeamDTO TeamDTO) {
        TeamDTO createdTeam = teamService.createTeam(TeamDTO);
        return Response.status(Response.Status.CREATED)
                .entity(new GeneralResponseDTO<>(true, Response.Status.CREATED.getStatusCode(), null, createdTeam))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({OWNER, ADMIN, MANAGER})
    @Operation(summary = "Update team", description = "Return updated team")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Successful, team updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response updateTeam(TeamDTO teamDTO, @PathParam("id") UUID id) {
        TeamDTO updatedTeam = teamService.updateTeam(teamDTO, id);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, updatedTeam))
                .build();
    }

        @GET
        @RolesAllowed({OWNER, ADMIN, MANAGER, USER})
    @Operation(summary = "Create a new team", description = "Return all teams")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Successful, teams returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response getAllTeams() {
        List<TeamDTO> TeamDTOs = teamService.getTeams();
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, TeamDTOs))
                .build();
    }

        @GET()
        @Path("/{id}")
        @RolesAllowed({OWNER, ADMIN, MANAGER, USER})
    @Operation(summary = "Create a new team", description = "Return team based on given id")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Successful, team returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response getTeamById(@PathParam("id") UUID id) {
        TeamDTO teamDTO = teamService.getTeamById(id);
        return Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, teamDTO))
                .build();
    }

    @DELETE()
    @Path("/{id}")
    @RolesAllowed({OWNER, ADMIN, MANAGER})
    @Operation(summary = "Create a new team", description = "Return status ok if team deleted")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Successful, team deleted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response deleteTeamById(@PathParam("id") UUID id) {
        teamService.deleteTeamById(id);
        return  Response.status(Response.Status.OK)
                .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
                .build();
    }

    @POST
    @Path("/{teamId}/employees/{employeeId}/roles/{teamRole}")
    @RolesAllowed({OWNER, ADMIN, MANAGER})
    @Operation(summary = "Add new member to team", description = "Add new member to team")
    @APIResponses({
            @APIResponse(responseCode = "200",
                         description = "Successful, team member added",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = TeamDTO.class))),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public Response addEmployeeToTeam(@PathParam("teamId") UUID teamId,
                                      @PathParam("employeeId") UUID employeeId,
                                      @PathParam("teamRole") TeamRole teamRole) {
        teamService.addEmployeeToTeam(teamId, employeeId, teamRole);
        return Response.status(Response.Status.OK)
                       .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
                       .build();
    }
}
