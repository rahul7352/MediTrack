package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class CSVUtil {

    private CSVUtil() {
    }

    public static void ensureDataDir(String dirPath) throws IOException {
        Files.createDirectories(Paths.get(dirPath));
    }

    public static void saveDoctors(String path, List<Doctor> doctors) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id,name,age,phone,specialization,consultationFee");
            writer.newLine();
            for(Doctor doctor : doctors) {
                writer.write(String.join(", ",
                        doctor.getId(), doctor.getName(), String.valueOf(doctor.getAge()),
                        doctor.getPhone(), doctor.getSpecialization().name(), String.valueOf(doctor.getConsultationFee())));
                writer.newLine();
            }
        }
    }

    public static List<Doctor> loadDoctors(String path) throws IOException, InvalidDataException {
        List<Doctor> doctors = new ArrayList<>();
        if(!Files.exists(Paths.get(path))) {
            return doctors;
        }
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            while((line = reader.readLine()) != null) {
                if(line.isBlank()) {
                    continue;
                }
                String[] splittedArray = line.split(",");
                doctors.add(new Doctor(splittedArray[0], splittedArray[1],
                        Integer.parseInt(splittedArray[2]), splittedArray[3],
                                Specialization.valueOf(splittedArray[4]), Double.parseDouble(splittedArray[5])));
            }
        }
        return doctors;
    }

    public static void savePatients(String path, List<Patient> patients) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id,name,age,phone,bloodGroup,insured");
            writer.newLine();
            for(Patient patient : patients) {
                writer.write(String.join(", ",
                        patient.getId(), patient.getName(), String.valueOf(patient.getAge()),
                                patient.getPhone(), patient.getBloodGroup(), String.valueOf(patient.isInsured())));
                writer.newLine();
            }
        }
    }

    public static List<Patient> loadPatients(String path) throws IOException, InvalidDataException {
        List<Patient> patients = new ArrayList<>();
        if(!Files.exists(Paths.get(path))) {
            return patients;
        }
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            while((line = reader.readLine()) != null) {
                if(line.isBlank()) {
                    continue;
                }
                String[] patientData = line.split(",");
                patients.add(new Patient(patientData[0], patientData[1],
                        Integer.parseInt(patientData[2]), patientData[3],
                        patientData[4], Boolean.parseBoolean(patientData[5])));
            }
        }
        return patients;
    }
}
