package com.airtribe.meditrack.constants;

/**
 * Application-wide constants. All fields are static final so this class
 * is never instantiated - it acts purely as a config holder.
 */
public final class Constants {

    // Prevent instantiation
    private Constants() {
    }

    public static final double DEFAULT_TAX_RATE = 0.05; // 5%
    public static final double INSURANCE_DISCOUNT_RATE = 0.20; // 20% off for insured patients

    public static final String DATA_DIR = "data";
    public static final String PATIENTS_CSV = DATA_DIR + "/patients.csv";
    public static final String DOCTORS_CSV = DATA_DIR + "/doctors.csv";
    public static final String APPOINTMENTS_CSV = DATA_DIR + "/appointments.csv";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static final String LOAD_DATA_FLAG = "--loadData";
}
