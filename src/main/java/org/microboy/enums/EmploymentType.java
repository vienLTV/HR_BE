package org.microboy.enums;

public enum EmploymentType {
    FULL_TIME,
    PART_TIME,
    REMOTE;

    public static EmploymentType fromString(String value) {
        try {
            return value == null
                ? null
                : EmploymentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
