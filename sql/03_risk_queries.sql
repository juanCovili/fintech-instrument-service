-- =============================================================================
-- Advanced Capital Markets Queries (Mark-to-Market, PnL & Exposure)
-- =============================================================================

-- 1. Mark-to-Market (MTM) Portfolio Valuation and Unrealized Profit/Loss (PnL)
SELECT 
    p.name AS portfolio_name,
    fi.ticker,
    fi.instrument_type,
    pos.quantity,
    pos.average_entry_price,
    mp.price AS current_market_price,
    (pos.quantity * pos.average_entry_price) AS book_cost_total,
    (pos.quantity * mp.price) AS current_market_value,
    ((pos.quantity * mp.price) - (pos.quantity * pos.average_entry_price)) AS unrealized_pnl,
    ROUND((((mp.price - pos.average_entry_price) / pos.average_entry_price) * 100), 2) AS return_percentage
FROM portfolio_positions pos
JOIN portfolios p ON pos.portfolio_id = p.id
JOIN financial_instruments fi ON pos.instrument_isin = fi.isin
JOIN market_prices mp ON fi.isin = mp.instrument_isin
ORDER BY p.name, current_market_value DESC;

-- 2. Exposure by Issuer (Concentration Risk Analysis)
SELECT 
    fi.issuer_name,
    fi.instrument_type,
    SUM(pos.quantity * mp.price) AS total_exposure_value,
    ROUND(
        SUM(pos.quantity * mp.price) * 100.0 / 
        (SELECT SUM(p2.quantity * m2.price) FROM portfolio_positions p2 JOIN market_prices m2 ON p2.instrument_isin = m2.instrument_isin), 
        2
    ) AS portfolio_exposure_percentage
FROM portfolio_positions pos
JOIN financial_instruments fi ON pos.instrument_isin = fi.isin
JOIN market_prices mp ON fi.isin = mp.instrument_isin
GROUP BY fi.issuer_name, fi.instrument_type
ORDER BY total_exposure_value DESC;
