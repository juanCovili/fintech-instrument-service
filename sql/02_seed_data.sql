-- =============================================================================
-- Seed Data for Capital Markets & Fixed Income / Equities
-- =============================================================================

INSERT INTO portfolios (id, name, currency, risk_profile) VALUES
(1, 'Fondo Renta Fija Soberana', 'CLP', 'CONSERVATIVE'),
(2, 'Cartera Mixta Institucional', 'USD', 'BALANCED')
ON CONFLICT (id) DO NOTHING;

INSERT INTO financial_instruments (isin, ticker, issuer_name, instrument_type, currency, nominal_value, coupon_rate, maturity_date) VALUES
('CL0002456781', 'BCU-10Y', 'Banco Central de Chile', 'BOND', 'CLP', 1000.00, 0.0450, '2034-03-01'),
('US912828ZG88', 'US-T10Y', 'US Department of Treasury', 'BOND', 'USD', 1000.00, 0.0425, '2034-11-15'),
('CL0000000105', 'CHILE', 'Banco de Chile', 'EQUITY', 'CLP', 0.00, 0.0820, NULL),
('CL0001856728', 'SQM-B', 'Sociedad Química y Minera de Chile', 'EQUITY', 'USD', 0.00, 0.0550, NULL)
ON CONFLICT (isin) DO NOTHING;

INSERT INTO portfolio_positions (portfolio_id, instrument_isin, quantity, average_entry_price) VALUES
(1, 'CL0002456781', 500.00, 995.00),
(2, 'US912828ZG88', 250.00, 980.50),
(2, 'CL0000000105', 15000.00, 110.00),
(2, 'CL0001856728', 400.00, 41000.00);

INSERT INTO market_prices (instrument_isin, price, price_date, source) VALUES
('CL0002456781', 1025.00, CURRENT_DATE, 'BOLSA_SANTIAGO'),
('US912828ZG88', 992.50, CURRENT_DATE, 'NEW_YORK_FED'),
('CL0000000105', 115.40, CURRENT_DATE, 'BOLSA_SANTIAGO'),
('CL0001856728', 42500.00, CURRENT_DATE, 'BOLSA_SANTIAGO');
