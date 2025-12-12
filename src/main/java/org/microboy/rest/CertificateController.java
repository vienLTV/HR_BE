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
import org.microboy.dto.CertificateDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.service.CertificateService;

import java.util.List;
import java.util.UUID;

import static org.microboy.security.constants.RoleConstants.ADMIN;
import static org.microboy.security.constants.RoleConstants.MANAGER;

@Path(("/certificates"))
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Certificate", description = "Operations related to certificates")
@RequiredArgsConstructor
public class CertificateController {

	private final CertificateService certificateService;

	@GET
	@RolesAllowed({ADMIN, MANAGER})
	@Operation(summary = "Get all certificates", description = "Returns a list of all certificates")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, returns all certificates",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = CertificateDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findAllCertificate() {
		List<CertificateDTO> certificates = certificateService.findAllCertificate();
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, certificates))
		               .build();
	}

	@POST
	@RolesAllowed({ADMIN, MANAGER})
	@Operation(summary = "Create a new certificate", description = "Return certificate that is created")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, certificate created",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = CertificateDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response createCertificate(CertificateDTO certificate) {
		CertificateDTO createdCertificate = certificateService.createCertificate(certificate);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                createdCertificate))
		               .build();
	}

	@GET
	@Path("/{id}")
	@RolesAllowed({ADMIN, MANAGER})
	@Operation(summary = "Get certificate based on given id", description = "Return certificate with given id")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, certificate returned",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = CertificateDTO.class))),
			@APIResponse(responseCode = "404", description = "Certificate not found from database"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findById(@PathParam("id") UUID id) {
		CertificateDTO CertificateDTO = certificateService.findCertificateById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                CertificateDTO)).build();
	}

	@PUT
	@Path("/{id}")
	@RolesAllowed({ADMIN, MANAGER})
	@Operation(summary = "Update certificate based on given id and info", description = "Return certificate updated")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, certificate updated",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = CertificateDTO.class))),
			@APIResponse(responseCode = "404", description = "Certificate not found to be updated"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response update(CertificateDTO certificateDTO, @PathParam("id") UUID id) {
		certificateDTO = certificateService.updateCertificate(certificateDTO, id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                certificateDTO))
		               .build();
	}

	@DELETE
	@Path("/{id}")
	@RolesAllowed({ADMIN, MANAGER})
	@Operation(summary = "Delete certificate based on given id", description = "Return status ok")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, certificate deleted",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = CertificateDTO.class))),
			@APIResponse(responseCode = "404", description = "Certificate not found to be deleted"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response deleteById(@PathParam("id") UUID id) {
		certificateService.deleteCertificateById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
		               .build();
	}

}
