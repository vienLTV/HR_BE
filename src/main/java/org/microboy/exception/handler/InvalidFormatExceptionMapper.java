package org.microboy.exception.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.microboy.exception.ErrorMessage;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

	@Override
	public Response toResponse(InvalidFormatException e) {
		return Response.status(Response.Status.BAD_REQUEST)
		               .entity(new ErrorMessage(false,
		                                        Response.Status.BAD_REQUEST.getStatusCode(),
		                                        e.getMessage(),
		                                        e.getStackTrace()))
		               .build();
	}
}
