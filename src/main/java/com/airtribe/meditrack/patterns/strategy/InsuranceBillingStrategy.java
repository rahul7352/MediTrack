package com.airtribe.meditrack.patterns.strategy;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Bill;

public class InsuranceBillingStrategy implements BillingStrategy {

    @Override
    public void applyCharges(Bill bill) {
        bill.setTaxAmount(bill.getBaseAmount() * Constants.DEFAULT_TAX_RATE);
        bill.setDiscountAmount(bill.getBaseAmount() * Constants.INSURANCE_DISCOUNT_RATE);
    }
}
