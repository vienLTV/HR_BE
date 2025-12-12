package org.microboy.enums;

public enum ContactType {

    FAMILY,
    FRIEND,
    SPOUSE,
    COLLEAGUE,
    OTHER;

    public static ContactType fromString(String value) {
        try {
            return value == null ? null : ContactType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;  // Return null if value does not match any enum value
        }
    }

}
