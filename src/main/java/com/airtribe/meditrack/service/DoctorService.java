package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.util.DataStore;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorService {

    private final DataStore<Doctor> doctorDataStore = new DataStore<>(Doctor::getId);

    public void addDoctor(Doctor doctor) {
        doctorDataStore.add(doctor);
    }

    public Doctor getDoctorById(String doctorId) {
        return doctorDataStore.getById(doctorId);
    }

    public List<Doctor> getAllDoctors() {
        return doctorDataStore.getAll();
    }

    public void deleteDoctor(String doctorId) {
        doctorDataStore.delete(doctorId);
    }

    public boolean updateDoctor(Doctor doctor) {
        return doctorDataStore.update(doctor);
    }

    public List<Doctor> searchDoctor(String query) {
        return doctorDataStore.getAll().stream()
                .filter(d -> d.matches(query))
                .collect(Collectors.toList());
    }

    public List<Doctor> filterBySpecialization(Specialization specialization) {
        return doctorDataStore.getAll().stream()
                .filter(d -> d.getSpecialization() == specialization)
                .sorted(Comparator.comparing(Doctor::getName))
                .collect(Collectors.toList());
    }

    public double calculateAverageConsultationFee() {
        return doctorDataStore.getAll().stream()
                .mapToDouble(Doctor::getConsultationFee)
                .average()
                .orElse(0.0);
    }

    public DataStore<Doctor> getDoctorDataStore() {
        return doctorDataStore;
    }
}
