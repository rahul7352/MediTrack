package com.airtribe.meditrack.patterns.factory;

import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.patterns.strategy.BillingStrategy;
import com.airtribe.meditrack.patterns.strategy.InsuranceBillingStrategy;
import com.airtribe.meditrack.patterns.strategy.StandardBillingStrategy;
import com.airtribe.meditrack.util.IdGenerator;

public final class BillFactory {

    private BillFactory() {
    }

    public static Bill createBill(String appointmentId, double baseAmount, boolean insured) {
        Bill bill = new Bill(IdGenerator.getInstance().nextBillId(), appointmentId, baseAmount);
        BillingStrategy billingStrategy = insured ? new InsuranceBillingStrategy() : new StandardBillingStrategy();
        billingStrategy.applyCharges(bill);
        return bill;
    }
}
