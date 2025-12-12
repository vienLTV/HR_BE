package org.microboy.enums;

public enum EmployeeStatus {
	OFFICIAL,
	PROBATION;

	public static EmployeeStatus fromString(String value) {
		try {
			return value == null ? null : EmployeeStatus.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;  // Return null if value does not match any enum value
		}
	}
}
