/**
 * @project   Fintech Core Instruments & Risk Valuation Service
 * @author    Juan Covili <https://github.com/juanCovili>
 * @repo      https://github.com/juanCovili/fintech-instrument-service
 * @portfolio https://juancovili.com
 * @copyright Copyright (c) 2026 Juan Covili. Todos los derechos reservados.
 */
package com.juancovili.fintech.service;

import com.juancovili.fintech.model.FinancialInstrument;

/**
 * Strategy Pattern: Defines an algorithm family for valuing different financial instruments.
 */
public interface ValuationStrategy {
    double calculateFairValue(FinancialInstrument instrument, double discountRate);
}
