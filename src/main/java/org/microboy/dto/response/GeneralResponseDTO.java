package org.microboy.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RegisterForReflection
public class GeneralResponseDTO<T> implements Serializable {

	@Serial
	private static final long serialVersionUID = -8458557998612166861L;

	private boolean success;
	private long code;
	private String message;
	private T data;
}
