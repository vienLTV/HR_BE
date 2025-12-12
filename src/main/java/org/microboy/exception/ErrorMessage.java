package org.microboy.exception;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class ErrorMessage implements Serializable {

	@Serial
	private static final long serialVersionUID = 3775129518581328655L;

	private boolean success;
	private long code;
	private String message;
	private StackTraceElement[] stackTraceElements;
}
