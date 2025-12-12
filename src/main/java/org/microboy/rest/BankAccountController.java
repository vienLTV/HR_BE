package org.microboy.rest;

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
import org.microboy.dto.BankAccountDTO;
import org.microboy.dto.response.GeneralResponseDTO;
import org.microboy.security.utils.OwnerAdminManagerAllowed;
import org.microboy.service.BankAccountService;

import java.util.List;
import java.util.UUID;

@Path(("/bank-accounts"))
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Bank Account", description = "Operations related to bank accounts")
@RequiredArgsConstructor
public class BankAccountController {

	private final BankAccountService bankAccountService;

	@GET
	@OwnerAdminManagerAllowed
	@Operation(summary = "Get all bank accounts", description = "Returns a list of all bank accounts")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, returns all bank accounts",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = BankAccountDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findAllBankAccounts() {
		List<BankAccountDTO> bankAccounts = bankAccountService.findAllBankAccount();
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, bankAccounts))
		               .build();
	}

	@POST
	@OwnerAdminManagerAllowed
	@Operation(summary = "Create a new bank account", description = "Return bank account that is created")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, bank account created",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = BankAccountDTO.class))),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response createBankAccount(BankAccountDTO bankAccountDTO) {
		BankAccountDTO createdBankAccount = bankAccountService.createBankAccount(bankAccountDTO);
		return Response.status(Response.Status.CREATED)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.CREATED.getStatusCode(),
		                                                null,
		                                                createdBankAccount))
		               .build();
	}

	@GET
	@Path("/{id}")
	@OwnerAdminManagerAllowed
	@Operation(summary = "Get bank account based on given id", description = "Return bank account with given id")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, bank account returned",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = BankAccountDTO.class))),
			@APIResponse(responseCode = "404", description = "Bank Account not found from database"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response findByEmployeeId(@PathParam("id") UUID employeeId) {
		var bankAccountDTOS = bankAccountService.findBankAccountByEmployeeId(employeeId);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                bankAccountDTOS))
		               .build();
	}


	@PUT
	@Path("/{id}")
	@OwnerAdminManagerAllowed
	@Operation(summary = "Update bank account based on given id and info", description = "Return bank account updated")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, bank account updated",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = BankAccountDTO.class))),
			@APIResponse(responseCode = "404", description = "Bank account not found to be updated"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response update(BankAccountDTO bankAccountDTO, @PathParam("id") UUID id) {
		bankAccountDTO = bankAccountService.updateBankAccount(bankAccountDTO, id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true,
		                                                Response.Status.OK.getStatusCode(),
		                                                null,
		                                                bankAccountDTO))
		               .build();
	}

	@DELETE
	@Path("/{id}")
	@OwnerAdminManagerAllowed
	@Operation(summary = "Delete bank account based on given id", description = "Return status ok")
	@APIResponses({
			@APIResponse(responseCode = "200",
			             description = "Successful, bank account deleted",
			             content = @Content(mediaType = "application/json",
			                                schema = @Schema(implementation = BankAccountDTO.class))),
			@APIResponse(responseCode = "404", description = "Bank account not found to be deleted"),
			@APIResponse(responseCode = "500", description = "Internal server error")
	})
	public Response deleteById(@PathParam("id") UUID id) {
		bankAccountService.deleteBankAccountById(id);
		return Response.status(Response.Status.OK)
		               .entity(new GeneralResponseDTO<>(true, Response.Status.OK.getStatusCode(), null, null))
		               .build();
	}
}
