package com.airtribe.meditrack.util;

import com.airtribe.meditrack.exception.InvalidDataException;

public final class Validator {

    private Validator() {}

    public static void validateName(String name) throws InvalidDataException {
        if(name ==  null || name.trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be null or empty");
        }

        if(!name.matches("[a-zA-Z .]+")) {
            throw new InvalidDataException("Name can only contain letters, spaces, and periods.");
        }
    }

    public static void validateAge(int age) throws InvalidDataException {
        if(age <= 0 || age > 120) {
            throw new InvalidDataException("Age must be between 1 and 120.");
        }
    }

    public static void validatePhone(String phone) throws InvalidDataException {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new InvalidDataException("Phone number must be exactly 10 digits.");
        }
    }

    public static void validateFee(double fee) throws InvalidDataException {
        if (fee < 0) {
            throw new InvalidDataException("Fee cannot be negative.");
        }
    }

    public static void validateNotEmpty(String value, String fieldName) throws InvalidDataException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(fieldName + " cannot be empty.");
        }
    }
}
