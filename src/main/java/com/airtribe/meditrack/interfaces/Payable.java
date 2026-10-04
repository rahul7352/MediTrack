package com.airtribe.meditrack.interfaces;

public interface Payable {

    double getAmount();

    default String getFormattedAmount() {
        return String.format("Rs. %.2f", getAmount());
    }
}
