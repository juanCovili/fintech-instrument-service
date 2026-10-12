package com.juancovili.fintech.model;

/**
 * Base abstract class demonstrating Object-Oriented Principles (Encapsulation and Inheritance).
 */
public abstract class FinancialInstrument {
    private final String isin;
    private final String ticker;
    private final String issuer;
    private final InstrumentType type;
    private double quantity;
    private double marketPrice;

    public FinancialInstrument(String isin, String ticker, String issuer, InstrumentType type, double quantity, double marketPrice) {
        this.isin = isin;
        this.ticker = ticker;
        this.issuer = issuer;
        this.type = type;
        this.quantity = quantity;
        this.marketPrice = marketPrice;
    }

    public String getIsin() { return isin; }
    public String getTicker() { return ticker; }
    public String getIssuer() { return issuer; }
    public InstrumentType getType() { return type; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getMarketPrice() { return marketPrice; }
    public void setMarketPrice(double marketPrice) { this.marketPrice = marketPrice; }

    public abstract double calculateBookValue();
}
