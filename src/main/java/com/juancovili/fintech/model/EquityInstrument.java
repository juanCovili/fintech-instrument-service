/**
 * @project   Fintech Core Instruments & Risk Valuation Service
 * @author    Juan Covili <https://github.com/juanCovili>
 * @repo      https://github.com/juanCovili/fintech-instrument-service
 * @portfolio https://juancovili.com
 * @copyright Copyright (c) 2026 Juan Covili. Todos los derechos reservados.
 */
package com.juancovili.fintech.model;

/**
 * Variable income equity/stock instrument with dividend yield.
 */
public class EquityInstrument extends FinancialInstrument {
    private final double dividendYield;

    public EquityInstrument(String isin, String ticker, String issuer, double quantity, double marketPrice, double dividendYield) {
        super(isin, ticker, issuer, InstrumentType.EQUITY, quantity, marketPrice);
        this.dividendYield = dividendYield;
    }

    public double getDividendYield() { return dividendYield; }

    @Override
    public double calculateBookValue() {
        return getQuantity() * getMarketPrice();
    }
}
