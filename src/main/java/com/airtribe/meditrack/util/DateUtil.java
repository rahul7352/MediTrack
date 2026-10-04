package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.exception.InvalidDataException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    private DateUtil() {
    }

    public static LocalDate parseDate(String value) throws InvalidDataException {
        try {
            return LocalDate.parse(value, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("Date must be in format " + Constants.DATE_FORMAT, e);
        }
    }

    public static LocalDateTime parseDateTime(String value) throws InvalidDataException {
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("Date/time must be in format " + Constants.DATE_TIME_FORMAT, e);
        }
    }

    public static String format(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
