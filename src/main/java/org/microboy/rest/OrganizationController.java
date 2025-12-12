package org.microboy.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
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
import org.microboy.dto.JobTitleDTO;
import org.microboy.dto.request.SignUpRequestDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.service.SignUpService;

@Path(("/sign-up"))
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Organization", description = "Operations related to Organization")
@RequiredArgsConstructor
public class OrganizationController {
	private final SignUpService signUpService;

	@POST
	@PermitAll
	@Operation(summary = "Create a new Organization", description = "Return status that is created")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, Organization created",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = JobTitleDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response createOrgAndOwnerAccount(SignUpRequestDTO signUpRequestDTO) {
		signUpService.createOrgAndOwnerAccount(signUpRequestDTO);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                null))
		               .build();
	}
}
