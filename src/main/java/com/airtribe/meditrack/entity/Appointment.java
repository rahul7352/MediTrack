package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.interfaces.Searchable;

import java.time.LocalDateTime;

public class Appointment extends MedicalEntity implements Cloneable {

    private final String appointmentId;
    private final String patientId;
    private final String doctorId;
    private final LocalDateTime scheduledAt;
    private AppointmentStatus status;
    private Bill bill;

    public Appointment(String appointmentId, String patientId, String doctorId, LocalDateTime scheduledAt) {
        super();
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.scheduledAt = scheduledAt;
        status = AppointmentStatus.PENDING;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public Bill getBill() {
        return bill;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public String displayInfo() {
        return String.format("Appointment[id=%s, doctorId=%s, patientId=%s, time=%s, status=%s]",
                appointmentId, doctorId, patientId, scheduledAt, status);
    }

    @Override
    public String toString() {
        return displayInfo();
    }

    // clone the appointment object
    @Override
    public Appointment clone() {
        try {
            Appointment copy = (Appointment) super.clone();
            if (this.bill != null) {
                copy.bill = this.bill.clone();
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Appointment must be cloneable", e);
        }
    }
}
