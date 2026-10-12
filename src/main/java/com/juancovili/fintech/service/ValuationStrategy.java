package com.juancovili.fintech.service;

import com.juancovili.fintech.model.FinancialInstrument;

/**
 * Strategy Pattern: Defines an algorithm family for valuing different financial instruments.
 */
public interface ValuationStrategy {
    double calculateFairValue(FinancialInstrument instrument, double discountRate);
}
