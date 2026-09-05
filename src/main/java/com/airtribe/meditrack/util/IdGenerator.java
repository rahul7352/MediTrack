package com.airtribe.meditrack.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {

    private static final IdGenerator INSTANCE = new IdGenerator();

    private final AtomicInteger patientCounter = new AtomicInteger(0);
    private final AtomicInteger doctorCounter = new AtomicInteger(0);
    private final AtomicInteger appointmentCounter = new AtomicInteger(0);
    private final AtomicInteger billCounter = new AtomicInteger(0);

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    public String nextPatientId() {
        return "PAT-" + patientCounter.incrementAndGet();
    }

    public String nextDoctorId() {
        return "DOC-" + doctorCounter.incrementAndGet();
    }

    public String nextAppointmentId() {
        return "APT-" + appointmentCounter.incrementAndGet();
    }

    public String nextBillId() {
        return "BILL-" + billCounter.incrementAndGet();
    }
}
