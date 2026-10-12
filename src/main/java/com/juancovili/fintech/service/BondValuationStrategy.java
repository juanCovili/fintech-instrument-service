package com.juancovili.fintech.service;

import com.juancovili.fintech.model.BondInstrument;
import com.juancovili.fintech.model.FinancialInstrument;

/**
 * Fixed income discounted cash flow valuation algorithm.
 */
public class BondValuationStrategy implements ValuationStrategy {
    @Override
    public double calculateFairValue(FinancialInstrument instrument, double discountRate) {
        if (!(instrument instanceof BondInstrument)) {
            throw new IllegalArgumentException("Instrument must be an instance of BondInstrument");
        }
        BondInstrument bond = (BondInstrument) instrument;
        double annualCoupon = bond.getNominalValue() * bond.getCouponRate();
        double presentValueCoupons = 0.0;
        int years = bond.getMaturYears();

        for (int t = 1; t <= years; t++) {
            presentValueCoupons += annualCoupon / Math.pow(1.0 + discountRate, t);
        }
        double presentValuePrincipal = bond.getNominalValue() / Math.pow(1.0 + discountRate, years);
        return (presentValueCoupons + presentValuePrincipal) * bond.getQuantity();
    }
}
