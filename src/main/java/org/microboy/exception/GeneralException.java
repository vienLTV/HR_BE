package org.microboy.exception;

import java.io.Serial;

public class GeneralException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public GeneralException(String message) {
		super(message);
	}

	public GeneralException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeneralException(Throwable cause) {
		super(cause);
	}

	public GeneralException(String message, Throwable cause,
	                       boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
