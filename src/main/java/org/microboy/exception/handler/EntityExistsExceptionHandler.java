package org.microboy.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.microboy.exception.ErrorMessage;

@Provider
public class EntityExistsExceptionHandler implements ExceptionMapper<EntityNotFoundException> {

	@Override
	public Response toResponse(EntityNotFoundException exception) {
		return Response.status(Response.Status.NOT_FOUND)
		               .entity(new ErrorMessage(false,
		                                        Response.Status.BAD_REQUEST.getStatusCode(),
		                                        exception.getMessage(),
		                                        exception.getStackTrace()))
		               .build();
	}
}
