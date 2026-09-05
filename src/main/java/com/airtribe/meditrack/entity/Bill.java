package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.interfaces.Payable;

public class Bill extends MedicalEntity implements Payable, Cloneable {

    private final String billId;
    private final String appointmentId;
    private double baseAmount;
    private double taxAmount;
    private double discountAmount;

    public Bill(String billId, String appointmentId, double baseAmount) {
        super();
        this.billId = billId;
        this.appointmentId = appointmentId;
        this.baseAmount = baseAmount;
    }

    public String getBillId() {
        return billId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BillSummary toBillSummary() {
        return new BillSummary(billId, appointmentId, getAmount());
    }

    @Override
    public String displayInfo() {
        return String.format("Bill[id=%s, appointmentId=%s, total=%s]", billId, appointmentId, getFormattedAmount());
    }

    @Override
    public double getAmount() {
        return baseAmount + taxAmount - discountAmount;
    }

    @Override
    public Bill clone() {
        try {
            return (Bill) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Bill must be cloneable", e);
        }
    }

    @Override
    public String toString() {
        return displayInfo();
    }
}
