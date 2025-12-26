package org.microboy.enums;

public enum AttendanceStatus {
	PENDING,
	PRESENT;

	public static AttendanceStatus fromString(String value) {
		try {
			return value == null ? null : AttendanceStatus.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;  // Return null if value does not match any enum value
		}
	}
}

