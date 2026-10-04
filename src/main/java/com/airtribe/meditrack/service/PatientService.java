package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.util.DataStore;

import java.util.List;
import java.util.stream.Collectors;

public class PatientService {

    private final DataStore<Patient> patientDataStore = new DataStore<>(Patient::getId);

    public void addPatient(Patient patient) {
        patientDataStore.add(patient);
    }

    public Patient getPatientById(String patientId) {
        return patientDataStore.getById(patientId);
    }

    public List<Patient> getAllPatients() {
        return patientDataStore.getAll();
    }

    public void deletePatient(String patientId) {
        patientDataStore.delete(patientId);
    }

    public boolean updatePatient(Patient patient) {
        return patientDataStore.update(patient);
    }

    // Overloading #1: search by free-text query (ID, name, or blood group).
    public List<Patient> searchPatient(String query) {
        return patientDataStore.getAll().stream()
                .filter(p -> p.matches(query))
                .collect(Collectors.toList());
    }

    // Overloading #2: search by exact age.
    public List<Patient> searchPatient(int age) {
        return patientDataStore.getAll().stream()
                .filter(p -> p.getAge() == age)
                .collect(Collectors.toList());
    }

    public DataStore<Patient> getPatientDataStore() {
        return patientDataStore;
    }
}
