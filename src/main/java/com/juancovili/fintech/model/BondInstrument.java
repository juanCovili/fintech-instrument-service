/**
 * @project   Fintech Core Instruments & Risk Valuation Service
 * @author    Juan Covili <https://github.com/juanCovili>
 * @repo      https://github.com/juanCovili/fintech-instrument-service
 * @portfolio https://juancovili.com
 * @copyright Copyright (c) 2026 Juan Covili. Todos los derechos reservados.
 */
package com.juancovili.fintech.model;

/**
 * Fixed income bond instrument with coupon and maturity characteristics.
 */
public class BondInstrument extends FinancialInstrument {
    private final double nominalValue;
    private final double couponRate;
    private final int maturityYears;

    public BondInstrument(String isin, String ticker, String issuer, double quantity, double marketPrice,
                          double nominalValue, double couponRate, int maturityYears) {
        super(isin, ticker, issuer, InstrumentType.BOND, quantity, marketPrice);
        this.nominalValue = nominalValue;
        this.couponRate = couponRate;
        this.maturityYears = maturityYears;
    }

    public double getNominalValue() { return nominalValue; }
    public double getCouponRate() { return couponRate; }
    public int getMaturYears() { return maturityYears; }

    @Override
    public double calculateBookValue() {
        return getQuantity() * nominalValue;
    }
}
