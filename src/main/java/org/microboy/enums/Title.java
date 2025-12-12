package org.microboy.enums;

public enum Title {
	MR,
	MRS,
	OTHERS;

	public static Title fromString(String value) {
		try {
			return value == null ? null : Title.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;  // Return null if value does not match any enum value
		}
	}
}
