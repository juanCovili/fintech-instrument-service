-- =============================================================================
-- VMetrix-Style Financial Instruments & Risk Schema (PostgreSQL / SQLite)
-- =============================================================================

CREATE TABLE IF NOT EXISTS portfolios (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    risk_profile VARCHAR(50) DEFAULT 'MODERATE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS financial_instruments (
    isin VARCHAR(12) PRIMARY KEY,
    ticker VARCHAR(20) NOT NULL,
    issuer_name VARCHAR(150) NOT NULL,
    instrument_type VARCHAR(20) NOT NULL CHECK (instrument_type IN ('BOND', 'EQUITY', 'DERIVATIVE')),
    currency VARCHAR(3) DEFAULT 'USD',
    nominal_value NUMERIC(15, 2) DEFAULT 0.00,
    coupon_rate NUMERIC(6, 4) DEFAULT 0.0000,
    maturity_date DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS portfolio_positions (
    id SERIAL PRIMARY KEY,
    portfolio_id INT REFERENCES portfolios(id) ON DELETE CASCADE,
    instrument_isin VARCHAR(12) REFERENCES financial_instruments(isin),
    quantity NUMERIC(15, 4) NOT NULL,
    average_entry_price NUMERIC(15, 4) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS market_prices (
    id SERIAL PRIMARY KEY,
    instrument_isin VARCHAR(12) REFERENCES financial_instruments(isin),
    price NUMERIC(15, 4) NOT NULL,
    price_date DATE NOT NULL,
    source VARCHAR(50) DEFAULT 'BLOOMBERG',
    CONSTRAINT unique_instrument_date UNIQUE (instrument_isin, price_date)
);

CREATE INDEX IF NOT EXISTS idx_instruments_type ON financial_instruments(instrument_type);
CREATE INDEX IF NOT EXISTS idx_positions_portfolio ON portfolio_positions(portfolio_id);
CREATE INDEX IF NOT EXISTS idx_prices_isin_date ON market_prices(instrument_isin, price_date);
