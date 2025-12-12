package org.microboy.enums;

import org.apache.commons.lang3.StringUtils;

public enum MaritalStatus {
	MARRIED,
	NOT_MARRIED;

	public static MaritalStatus fromString(String value) {
		try {
			return StringUtils.isEmpty(value) ? null : MaritalStatus.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;  // Return null if value does not match any enum value
		}
	}
}
