package com.airtribe.meditrack.patterns.observer;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.util.DateUtil;

public class ConsoleReminderObserver implements AppointmentObserver{

    @Override
    public void onAppointmentUpdated(Appointment appointment) {
        System.out.printf("[Reminder] Appointment %s is now %s (scheduled for %s)%n",
                appointment.getAppointmentId(), appointment.getStatus(), DateUtil.format(appointment.getScheduledAt()));
    }
}
