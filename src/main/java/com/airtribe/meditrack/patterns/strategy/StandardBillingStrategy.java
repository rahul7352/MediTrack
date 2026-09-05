package com.airtribe.meditrack.patterns.strategy;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Bill;

public class StandardBillingStrategy implements BillingStrategy {

    @Override
    public void applyCharges(Bill bill) {
        bill.setTaxAmount(bill.getBaseAmount() * Constants.DEFAULT_TAX_RATE);
        bill.setDiscountAmount(0.0);
    }
}
