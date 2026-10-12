package com.juancovili.fintech.repository;

import com.juancovili.fintech.model.FinancialInstrument;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Repository / Data Access Object (DAO) Pattern:
 * Decouples domain logic from persistence mechanisms.
 */
public class InstrumentRepository {
    private static final InstrumentRepository INSTANCE = new InstrumentRepository();
    private final List<FinancialInstrument> storage = new CopyOnWriteArrayList<>();

    private InstrumentRepository() {}

    public static InstrumentRepository getInstance() {
        return INSTANCE;
    }

    public void save(FinancialInstrument instrument) {
        storage.removeIf(i -> i.getIsin().equalsIgnoreCase(instrument.getIsin()));
        storage.add(instrument);
    }

    public void saveAll(List<FinancialInstrument> instruments) {
        for (FinancialInstrument i : instruments) {
            save(i);
        }
    }

    public List<FinancialInstrument> findAll() {
        return Collections.unmodifiableList(storage);
    }

    public Optional<FinancialInstrument> findByIsin(String isin) {
        return storage.stream().filter(i -> i.getIsin().equalsIgnoreCase(isin)).findFirst();
    }

    public double calculateTotalPortfolioValue() {
        return storage.stream().mapToDouble(FinancialInstrument::calculateBookValue).sum();
    }
}
