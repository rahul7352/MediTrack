package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.interfaces.Searchable;
import com.airtribe.meditrack.util.Validator;

public class Doctor extends Person implements Searchable {

    private Specialization specialization;
    private double consultationFee;

    public Doctor(String id, String name, int age, String phone,
                  Specialization specialization, double consultationFee) throws InvalidDataException {
        super(id, name, age, phone);
        Validator.validateFee(consultationFee);
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    @Override
    public String describe() {
        return String.format("Doctor[id=%s, name=%s, specialization=%s, fee=%.2f]",
                getId(), getName(), specialization, consultationFee);
    }

    @Override
    public boolean matches(String query) {
        return checkContainsIgnoreCase(getId(), query)
                || checkContainsIgnoreCase(getName(), query)
                || checkContainsIgnoreCase(specialization.name(), query);
    }
}
