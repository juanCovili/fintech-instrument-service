/**
 * @project   Fintech Core Instruments & Risk Valuation Service
 * @author    Juan Covili <https://github.com/juanCovili>
 * @repo      https://github.com/juanCovili/fintech-instrument-service
 * @portfolio https://juancovili.com
 * @copyright Copyright (c) 2026 Juan Covili. Todos los derechos reservados.
 */
package com.juancovili.fintech.service;

import com.juancovili.fintech.model.EquityInstrument;
import com.juancovili.fintech.model.FinancialInstrument;

/**
 * Market mark-to-market valuation strategy.
 */
public class EquityValuationStrategy implements ValuationStrategy {
    @Override
    public double calculateFairValue(FinancialInstrument instrument, double discountRate) {
        if (!(instrument instanceof EquityInstrument)) {
            throw new IllegalArgumentException("Instrument must be an instance of EquityInstrument");
        }
        EquityInstrument equity = (EquityInstrument) instrument;
        // Mark-to-market total portfolio value
        return equity.getQuantity() * equity.getMarketPrice();
    }
}
