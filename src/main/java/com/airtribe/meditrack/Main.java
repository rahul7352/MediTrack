package com.airtribe.meditrack;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.patterns.factory.BillFactory;
import com.airtribe.meditrack.patterns.observer.ConsoleReminderObserver;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.AIHelper;
import com.airtribe.meditrack.util.CSVUtil;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.IdGenerator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DoctorService doctorService = new DoctorService();
    private static final PatientService patientService = new PatientService();
    private static final AppointmentService appointmentService = new AppointmentService();

    static void main(String[] args) {
        appointmentService.addObserver(new ConsoleReminderObserver());

        if (containsLoadFlag(args)) {
            loadPersistedData();
        }

        System.out.println("=== Welcome to MediTrack ===");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = SCANNER.nextLine().trim();
            switch (choice) {
                case "1" -> addDoctor();
                case "2" -> addPatient();
                case "3" -> listDoctors();
                case "4" -> listPatients();
                case "5" -> searchDoctors();
                case "6" -> searchPatients();
                case "7" -> bookAppointment();
                case "8" -> viewAppointment();
                case "9" -> confirmAppointment();
                case "10" -> cancelAppointment();
                case "11" -> generateBill();
                case "12" -> showAnalytics();
                case "13" -> recommendDoctor();
                case "14" -> saveData();
                case "0" -> running = false;
                default -> System.out.println("Invalid option, try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static boolean containsLoadFlag(String[] args) {
        for (String arg : args) {
            if (arg.equals(Constants.LOAD_DATA_FLAG)) {
                return true;
            }
        }
        return false;
    }

    private static void printMenu() {
        System.out.println("""

                1.  Add Doctor
                2.  Add Patient
                3.  List Doctors
                4.  List Patients
                5.  Search Doctors
                6.  Search Patients
                7.  Book Appointment
                8.  View Appointment
                9.  Cancel Appointment
                10. Generate Bill
                11. Show Analytics (Streams)
                12. Recommend Doctor by Symptom (AI)
                13. Save Data to CSV
                0.  Exit
                Choose an option:""");
    }

    // ---------- Doctor / Patient management ----------

    private static void addDoctor() {
        try {
            System.out.print("Name: ");
            String name = SCANNER.nextLine();
            System.out.print("Age: ");
            int age = Integer.parseInt(SCANNER.nextLine());
            System.out.print("Phone (10 digits): ");
            String phone = SCANNER.nextLine();
            System.out.println("Specializations: " + java.util.Arrays.toString(Specialization.values()));
            System.out.print("Specialization: ");
            Specialization spec = Specialization.valueOf(SCANNER.nextLine().trim().toUpperCase());
            System.out.print("Consultation fee: ");
            double fee = Double.parseDouble(SCANNER.nextLine());

            Doctor doctor = new Doctor(IdGenerator.getInstance().nextDoctorId(), name, age, phone, spec, fee);
            doctorService.addDoctor(doctor);
            System.out.println("Added: " + doctor.describe());
        } catch (InvalidDataException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addPatient() {
        try {
            System.out.print("Name: ");
            String name = SCANNER.nextLine();
            System.out.print("Age: ");
            int age = Integer.parseInt(SCANNER.nextLine());
            System.out.print("Phone (10 digits): ");
            String phone = SCANNER.nextLine();
            System.out.print("Blood group: ");
            String bloodGroup = SCANNER.nextLine();
            System.out.print("Insured? (yes/no): ");
            boolean insured = SCANNER.nextLine().trim().equalsIgnoreCase("yes");

            Patient patient = new Patient(IdGenerator.getInstance().nextPatientId(), name, age, phone,
                    bloodGroup, insured);
            patientService.addPatient(patient);
            System.out.println("Added: " + patient.describe());
        } catch (InvalidDataException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No doctors registered yet.");
        }
        doctors.forEach(d -> System.out.println(d.describe()));
    }

    private static void listPatients() {
        List<Patient> patients = patientService.getAllPatients();
        if (patients.isEmpty()) {
            System.out.println("No patients registered yet.");
        }
        patients.forEach(p -> System.out.println(p.describe()));
    }

    private static void searchDoctors() {
        System.out.print("Search query: ");
        String query = SCANNER.nextLine();
        List<Doctor> results = doctorService.searchDoctor(query);
        if (results.isEmpty()) {
            System.out.println("No matching doctors.");
        }
        results.forEach(d -> System.out.println(d.describe()));
    }

    private static void searchPatients() {
        System.out.print("Search by (1) text query or (2) age? ");
        String mode = SCANNER.nextLine().trim();
        List<Patient> results;
        if (mode.equals("2")) {
            System.out.print("Age: ");
            int age = Integer.parseInt(SCANNER.nextLine());
            results = patientService.searchPatient(age); // overloaded by int
        } else {
            System.out.print("Query: ");
            results = patientService.searchPatient(SCANNER.nextLine()); // overloaded by String
        }
        if (results.isEmpty()) {
            System.out.println("No matching patients.");
        }
        results.forEach(p -> System.out.println(p.describe()));
    }

    // ---------- Appointments ----------

    private static void bookAppointment() {
        System.out.print("Doctor ID: ");
        String doctorId = SCANNER.nextLine();
        System.out.print("Patient ID: ");
        String patientId = SCANNER.nextLine();

        if (doctorService.getDoctorById(doctorId) == null) {
            System.out.println("No such doctor. Aborting.");
            return;
        }
        Patient patient = patientService.getPatientById(patientId);
        if (patient == null) {
            System.out.println("No such patient. Aborting.");
            return;
        }

        try {
            System.out.print("Date/time (yyyy-MM-dd HH:mm): ");
            LocalDateTime dateTime = DateUtil.parseDateTime(SCANNER.nextLine());
            Appointment appointment = appointmentService.createAppointment(doctorId, patientId, dateTime);
            patient.addAppointment(appointment);
            System.out.println("Booked: " + appointment.displayInfo());
        } catch (InvalidDataException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAppointment() {
        System.out.print("Appointment ID: ");
        String id = SCANNER.nextLine();
        try {
            System.out.println(appointmentService.viewAppointment(id).displayInfo());
        } catch (AppointmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void confirmAppointment() {
        System.out.print("Appointment ID: ");
        String id = SCANNER.nextLine();
        try {
            appointmentService.confirmAppointment(id);
            System.out.println("Your appointment has been Confirmed.");
        } catch (AppointmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cancelAppointment() {
        System.out.print("Appointment ID: ");
        String id = SCANNER.nextLine();
        try {
            appointmentService.cancelAppointment(id);
            System.out.println("Cancelled.");
        } catch (AppointmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generateBill() {
        System.out.print("Appointment ID: ");
        String appointmentId = SCANNER.nextLine();
        try {
            Appointment appointment = appointmentService.viewAppointment(appointmentId);
            Doctor doctor = doctorService.getDoctorById(appointment.getDoctorId());
            Patient patient = patientService.getPatientById(appointment.getPatientId());
            if (doctor == null || patient == null) {
                System.out.println("Cannot bill: doctor or patient missing.");
                return;
            }
            Bill bill = BillFactory.createBill(appointmentId, doctor.getConsultationFee(), patient.isInsured());
            appointment.setBill(bill);
            System.out.println("Generated: " + bill.displayInfo());
            System.out.println("Summary: " + bill.toBillSummary());
        } catch (AppointmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------- Analytics / AI ----------

    private static void showAnalytics() {
        System.out.println("Average doctor fee: " + String.format("%.2f", doctorService.calculateAverageConsultationFee()));
        Map<String, Long> perDoctor = appointmentService.appointmentsPerDoctor();
        System.out.println("Appointments per doctor: " + perDoctor);
        System.out.println("Total persons created (Doctors + Patients): " + Person.getTotalPersonsCreated());
    }

    private static void recommendDoctor() {
        System.out.print("Describe symptoms: ");
        String symptoms = SCANNER.nextLine();
        Specialization recommended = AIHelper.getRecommendedSpecialization(symptoms);
        System.out.println("Recommended specialization: " + recommended);

        List<Doctor> matches = doctorService.filterBySpecialization(recommended);
        if (matches.isEmpty()) {
            System.out.println("No registered doctors in that specialization yet.");
        } else {
            matches.forEach(d -> System.out.println(" -> " + d.describe()));
        }
    }

    // ---------- Persistence ----------

    private static void saveData() {
        try {
            CSVUtil.ensureDataDir(Constants.DATA_DIR);
            CSVUtil.saveDoctors(Constants.DOCTORS_CSV, doctorService.getAllDoctors());
            CSVUtil.savePatients(Constants.PATIENTS_CSV, patientService.getAllPatients());
            System.out.println("Saved doctors and patients to CSV under " + Constants.DATA_DIR + "/");
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    private static void loadPersistedData() {
        try {
            List<Doctor> doctors = CSVUtil.loadDoctors(Constants.DOCTORS_CSV);
            doctors.forEach(doctorService::addDoctor);
            List<Patient> patients = CSVUtil.loadPatients(Constants.PATIENTS_CSV);
            patients.forEach(patientService::addPatient);
            System.out.printf("Loaded %d doctors and %d patients from CSV.%n", doctors.size(), patients.size());
        } catch (IOException | InvalidDataException e) {
            System.out.println("Could not load persisted data: " + e.getMessage());
        }
    }
}
