package com.airtribe.meditrack.exception;

public class AppointmentNotFoundException extends Exception {

    public AppointmentNotFoundException(String appointmentId) {
        super("No appointment found with ID: " + appointmentId);
    }

    public AppointmentNotFoundException(String appointmentId, Throwable cause) {
        super("No appointment found with ID: " + appointmentId, cause);
    }
}
