package org.microboy.exception.handler;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.microboy.exception.ErrorMessage;

@Provider
public class JsonParserExceptionHandler implements ExceptionMapper<JsonParseException> {

	@Override
	public Response toResponse(JsonParseException exception) {
		ErrorMessage errorMessage = new ErrorMessage(false,
		                                             Response.Status.BAD_REQUEST.getStatusCode(),
		                                             "Invalid JSON format: " + exception.getOriginalMessage(),
		                                             exception.getStackTrace());
		return Response.status(Response.Status.BAD_REQUEST)
		               .entity(errorMessage)
		               .build();
	}
}
