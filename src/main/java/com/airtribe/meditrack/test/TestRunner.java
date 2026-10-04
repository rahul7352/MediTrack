package com.airtribe.meditrack.test;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.patterns.factory.BillFactory;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;

/**
 * Manual test runner (no JUnit dependency required). Run directly to
 * sanity-check core flows; prints PASS/FAIL for each check.
 */
public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testDoctorCreationAndSearch();
        testPatientCloneIsDeep();
        testAppointmentLifecycle();
        testBillingStrategies();
        testAIRecommendation();

        System.out.printf("%n%d passed, %d failed%n", passed, failed);
    }

    private static void check(String label, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS - " + label);
        } else {
            failed++;
            System.out.println("FAIL - " + label);
        }
    }

    private static void testDoctorCreationAndSearch() {
        try {
            DoctorService doctorService = new DoctorService();
            Doctor d1 = new Doctor(IdGenerator.getInstance().nextDoctorId(), "Dr. Asha Rao", 40,
                    "9876543210", Specialization.CARDIOLOGY, 800);
            doctorService.addDoctor(d1);
            check("Doctor search by name finds result",
                    !doctorService.searchDoctor("Asha").isEmpty());
            check("Average fee matches single doctor's fee",
                    doctorService.calculateAverageConsultationFee() == 800.0);
        } catch (InvalidDataException e) {
            check("Doctor creation should not throw", false);
        }
    }

    private static void testPatientCloneIsDeep() {
        try {
            Patient original = new Patient(IdGenerator.getInstance().nextPatientId(), "Ravi Kumar",
                    30, "9123456780", "O+", false);
            Appointment appt = new Appointment(IdGenerator.getInstance().nextAppointmentId(),
                    "DOC-1", original.getId(), LocalDateTime.now());
            original.addAppointment(appt);

            Patient copy = original.clone();
            copy.getAppointmentHistory().get(0).setStatus(AppointmentStatus.CANCELLED);

            check("Deep clone: original appointment status unaffected by copy's mutation",
                    original.getAppointmentHistory().get(0).getStatus() == AppointmentStatus.PENDING);
        } catch (InvalidDataException e) {
            check("Patient creation should not throw", false);
        }
    }

    private static void testAppointmentLifecycle() {
        AppointmentService appointmentService = new AppointmentService();
        Appointment appt = appointmentService.createAppointment("DOC-1", "PAT-1", LocalDateTime.now().plusDays(1));
        try {
            appointmentService.confirmAppointment(appt.getAppointmentId());
            check("Appointment confirmed successfully",
                    appointmentService.viewAppointment(appt.getAppointmentId()).getStatus() == AppointmentStatus.CONFIRMED);

            appointmentService.cancelAppointment(appt.getAppointmentId());
            check("Appointment cancelled successfully",
                    appointmentService.viewAppointment(appt.getAppointmentId()).getStatus() == AppointmentStatus.CANCELLED);
        } catch (AppointmentNotFoundException e) {
            check("Appointment should be found", false);
        }

        try {
            appointmentService.viewAppointment("NON-EXISTENT-ID");
            check("Viewing missing appointment should throw", false);
        } catch (AppointmentNotFoundException e) {
            check("Viewing missing appointment throws AppointmentNotFoundException", true);
        }
    }

    private static void testBillingStrategies() {
        Bill standardBill = BillFactory.createBill("APT-1", 1000, false);
        Bill insuredBill = BillFactory.createBill("APT-2", 1000, true);

        check("Standard bill applies tax only", standardBill.getAmount() == 1050.0);
        check("Insured bill total is less than standard bill total",
                insuredBill.getAmount() < standardBill.getAmount());
    }

    private static void testAIRecommendation() {
        Specialization result = com.airtribe.meditrack.util.AIHelper.getRecommendedSpecialization("severe chest pain");
        check("AI recommends CARDIOLOGY for chest pain", result == Specialization.CARDIOLOGY);
    }
}
