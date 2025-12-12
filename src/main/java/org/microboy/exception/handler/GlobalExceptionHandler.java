package org.microboy.exception.handler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.microboy.exception.ErrorMessage;

@Provider
@Slf4j
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable throwable) {
		log.error(throwable.getMessage(), throwable);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		               .entity(new ErrorMessage(false,
		                                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
		                                        throwable.getMessage(),
		                                        throwable.getStackTrace()))
		               .build();
	}
}
