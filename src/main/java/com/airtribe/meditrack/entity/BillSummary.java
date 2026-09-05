package com.airtribe.meditrack.entity;

public final class BillSummary {

    private final String billId;
    private final String appointmentId;
    private final double totalAmount;

    public BillSummary(String billId, String appointmentId, double totalAmount) {
        this.billId = billId;
        this.appointmentId = appointmentId;
        this.totalAmount = totalAmount;
    }

    public String getBillId() {
        return billId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return String.format("BillSummary[billId=%s, appointmentId=%s, total=%.2f]",
                billId, appointmentId, totalAmount);
    }
}
