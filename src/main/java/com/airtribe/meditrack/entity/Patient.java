package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;

import java.util.ArrayList;
import java.util.List;

public class Patient extends Person implements Searchable, Cloneable {

    private String bloodGroup;
    private boolean insured;
    private List<Appointment> appointmentHistory;

    public Patient(String id, String name, int age, String phone,
                   String bloodGroup, boolean insured) throws InvalidDataException {
        super(id, name, age, phone);
        this.bloodGroup = bloodGroup;
        this.insured = insured;
        appointmentHistory = new ArrayList<>();
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public boolean isInsured() {
        return insured;
    }

    public List<Appointment> getAppointmentHistory() {
        return appointmentHistory;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void addAppointment(Appointment appointment) {
        appointmentHistory.add(appointment);
    }

    public void setInsured(boolean insured) {
        this.insured = insured;
    }

    @Override
    public String describe() {
        return String.format("Patient[id=%s, name=%s, age=%d, bloodGroup=%s, insured=%s, visits=%d]",
                getId(), getName(), getAge(), bloodGroup, insured, appointmentHistory.size());
    }

    @Override
    public boolean matches(String query) {
        return checkContainsIgnoreCase(getId(), query)
                || checkContainsIgnoreCase(getName(), query)
                || checkContainsIgnoreCase(bloodGroup, query);
    }

    // Override the clone method to create a deep copy of the Patient object
    @Override
    public Patient clone() {
        try {
            Patient copy = (Patient) super.clone();
            List<Appointment> deepCopiedHistory = new ArrayList<>();
            for (Appointment appt : this.appointmentHistory) {
                deepCopiedHistory.add(appt.clone());
            }
            copy.appointmentHistory = deepCopiedHistory;
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Patient must be cloneable", e);
        }
    }
}
