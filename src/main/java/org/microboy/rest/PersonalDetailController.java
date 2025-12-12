package org.microboy.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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
import org.microboy.dto.BankAccountDTO;
import org.microboy.dto.PersonalDetailDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.service.PersonalDetailService;

import java.util.UUID;

import static org.microboy.security.constants.RoleConstants.ADMIN;
import static org.microboy.security.constants.RoleConstants.MANAGER;
import static org.microboy.security.constants.RoleConstants.OWNER;

@Path(("/personal-details"))
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Personal Detail", description = "Operations related to personal detail")
@RequiredArgsConstructor
public class PersonalDetailController {

	private final PersonalDetailService personalDetailService;

	@GET
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Get personal detail", description = "Return personal detail")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, personal detail returned",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = BankAccountDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response getPersonalDetailById(@PathParam("id") UUID id) {
		PersonalDetailDTO personalDetailDTO = personalDetailService.getPersonalDetailById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                personalDetailDTO))
		               .build();
	}

	@POST
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Create a new personal detail", description = "Return personal detail that is created")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, personal detail created",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = BankAccountDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response createPersonalDetail(PersonalDetailDTO personalDetailDTO) {
		PersonalDetailDTO createdPersonalDetail = personalDetailService.createPersonalDetail(personalDetailDTO);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                createdPersonalDetail))
		               .build();
	}

	@PUT()
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Update personal detail", description = "Return personal detail that is updated")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, personal detail updated",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = BankAccountDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response updatePersonalDetail(PersonalDetailDTO personalDetailDTO, @PathParam("id") UUID id) {
		PersonalDetailDTO updatePersonalDetail = personalDetailService.updatePersonalDetail(personalDetailDTO, id);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                updatePersonalDetail))
		               .build();
	}
}
