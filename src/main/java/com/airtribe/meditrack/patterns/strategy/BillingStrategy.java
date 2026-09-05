package com.airtribe.meditrack.patterns.strategy;

import com.airtribe.meditrack.entity.Bill;

public interface BillingStrategy {

    void applyCharges(Bill bill);
}
