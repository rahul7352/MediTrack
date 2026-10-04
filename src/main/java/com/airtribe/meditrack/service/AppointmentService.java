package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.patterns.observer.AppointmentObserver;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppointmentService {

    private final DataStore<Appointment> appointmentDataStore = new DataStore<>(Appointment::getAppointmentId);
    private final List<AppointmentObserver> observers = new ArrayList<>();

    public void addObserver(AppointmentObserver observer) {
        observers.add(observer);
    }

    public void notifyObserver(Appointment appointment) {
        for(AppointmentObserver observer : observers) {
            observer.onAppointmentUpdated(appointment);
        }
    }

    public Appointment createAppointment(String doctorId, String patientId, LocalDateTime scheduledAt) {
        Appointment appointment = new Appointment(IdGenerator.getInstance().nextAppointmentId(),
                patientId, doctorId, scheduledAt);
        appointmentDataStore.add(appointment);
        notifyObserver(appointment);
        return appointment;
    }

    public Appointment viewAppointment(String appointmentId) throws AppointmentNotFoundException {
        Appointment appointment = appointmentDataStore.getById(appointmentId);
        if(appointment == null) {
            throw new AppointmentNotFoundException(appointmentId);
        }
        return appointment;
    }

    public void cancelAppointment(String appointmentId) throws AppointmentNotFoundException {
        Appointment appointment = viewAppointment(appointmentId);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        notifyObserver(appointment);
    }

    public void confirmAppointment(String appointmentId) throws AppointmentNotFoundException {
        Appointment appointment = viewAppointment(appointmentId);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        notifyObserver(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDataStore.getAll();
    }

    public DataStore<Appointment> getAppointmentDataStore() {
        return appointmentDataStore;
    }

    public List<Appointment> getAppointmentsForDoctor(String doctorId) {
        return appointmentDataStore.getAll().stream()
                .filter(d -> d.getDoctorId().equalsIgnoreCase(doctorId))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsForPatient(String patientId) {
        return appointmentDataStore.getAll().stream()
                .filter(p -> p.getPatientId().equalsIgnoreCase(patientId))
                .collect(Collectors.toList());
    }

    public Map<String, Long> appointmentsPerDoctor() {
        return appointmentDataStore.getAll().stream()
                .collect(Collectors.groupingBy(Appointment::getAppointmentId,
                        Collectors.counting()));
    }

    public List<LocalDateTime> suggestSlots(String doctorId, LocalDateTime from, int count) {
        List<LocalDateTime> bookedSlots = getAppointmentsForDoctor(doctorId).stream()
                .map(Appointment::getScheduledAt)
                .collect(Collectors.toList());

        List<LocalDateTime> suggestedSlots = new ArrayList<>();
        LocalDateTime currentSlot = from;
        while(suggestedSlots.size() < count) {
            if(!bookedSlots.contains(currentSlot)) {
                suggestedSlots.add(currentSlot);
            }
            currentSlot = currentSlot.plusMinutes(30); // Assuming 30-minute slots
        }
        return suggestedSlots;
    }

}
