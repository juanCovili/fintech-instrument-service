package com.juancovili.fintech.service;

import com.juancovili.fintech.model.BondInstrument;
import com.juancovili.fintech.model.EquityInstrument;
import com.juancovili.fintech.model.FinancialInstrument;
import com.juancovili.fintech.model.InstrumentType;

/**
 * Factory Pattern: Encapsulates instantiation logic for financial instruments.
 */
public class InstrumentFactory {

    public static FinancialInstrument createBond(String isin, String ticker, String issuer,
                                                 double quantity, double marketPrice,
                                                 double nominalValue, double couponRate, int maturityYears) {
        return new BondInstrument(isin, ticker, issuer, quantity, marketPrice, nominalValue, couponRate, maturityYears);
    }

    public static FinancialInstrument createEquity(String isin, String ticker, String issuer,
                                                   double quantity, double marketPrice, double dividendYield) {
        return new EquityInstrument(isin, ticker, issuer, quantity, marketPrice, dividendYield);
    }
}
