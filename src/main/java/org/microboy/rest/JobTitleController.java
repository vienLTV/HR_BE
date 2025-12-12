package org.microboy.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import org.microboy.dto.JobTitleDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.service.JobTitleService;

import java.util.List;
import java.util.UUID;

import static org.microboy.security.constants.RoleConstants.*;

@Path(("/job-titles"))
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Job Title", description = "Operations related to job titles")
@RequiredArgsConstructor
public class JobTitleController {

	private final JobTitleService jobTitleService;

	@POST
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Create a new job title", description = "Return job title that is created")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, job title created",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = JobTitleDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response createJobTitle(JobTitleDTO jobTitle) {
		JobTitleDTO createdJobTitle = jobTitleService.createJobTitle(jobTitle);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                createdJobTitle))
		               .build();
	}

	@GET
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Get all job titles", description = "Returns a list of all job titles")
	@APIResponses({
			@APIResponse(responseCode = "200",
					description = "Successful, returns all job titles",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = JobTitleDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findAllJobTitle() {
		List<JobTitleDTO> jobTitles = jobTitleService.findAllJobTitle();
		return Response.status(Response.Status.OK)
				.entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, jobTitles))
				.build();
	}

	@GET
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Get job title based on given id", description = "Return job title with given id")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, job title returned",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = JobTitleDTO.class))),
			@APIResponse(responseCode = "404", description = "Job title not found to be returned"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findById(@PathParam("id") UUID id) {
		JobTitleDTO jobTitle = jobTitleService.findJobTitleById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                jobTitle))
		               .build();
	}

	@PUT
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Update job title based on given id and info", description = "Return job title updated")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, job title updated",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = JobTitleDTO.class))),
			@APIResponse(responseCode = "404", description = "Job title not found to be updated"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response updateEmployee(JobTitleDTO jobTitle, @PathParam("id") UUID id) {
		jobTitle = jobTitleService.updateJobTitle(jobTitle, id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                jobTitle))
		               .build();
	}

	@DELETE
	@Path("/{id}")
	@RolesAllowed({OWNER, ADMIN, MANAGER})
	@Operation(summary = "Delete job title based on given id", description = "Return status ok")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, job title deleted",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = JobTitleDTO.class))),
			@APIResponse(responseCode = "404", description = "Job title not found to be deleted"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response deleteById(@PathParam("id") UUID id) {
		jobTitleService.deleteJobTitleById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
		               .build();
	}
}
