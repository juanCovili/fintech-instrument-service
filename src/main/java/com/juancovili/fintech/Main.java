package com.juancovili.fintech;

import com.juancovili.fintech.controller.InstrumentRestHandler;
import com.juancovili.fintech.model.FinancialInstrument;
import com.juancovili.fintech.repository.InstrumentRepository;
import com.juancovili.fintech.service.InstrumentFactory;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        // 1. Populate initial financial instruments seed
        InstrumentRepository repo = InstrumentRepository.getInstance();
        repo.save(InstrumentFactory.createBond("CL0002456781", "BCU-10Y", "Banco Central de Chile", 50, 102.5, 1000.0, 0.045, 10));
        repo.save(InstrumentFactory.createBond("US912828ZG88", "US-T10Y", "US Department of Treasury", 100, 98.2, 1000.0, 0.042, 5));
        repo.save(InstrumentFactory.createEquity("CL0000000105", "CHILE", "Banco de Chile", 12000, 115.4, 0.082));
        repo.save(InstrumentFactory.createEquity("CL0001856728", "SQM-B", "Sociedad Química y Minera de Chile", 350, 42500.0, 0.055));

        // 2. Start HTTP REST Web Service Server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/", new InstrumentRestHandler());
        server.createContext("/health", new InstrumentRestHandler());
        server.setExecutor(null);

        System.out.println("===============================================================");
        System.out.println("  FINTECH CORE INSTRUMENTS & RISK VALUATION SERVICE (JAVA 8+) ");
        System.out.println("  Running on port: " + PORT);
        System.out.println("  Endpoints:");
        System.out.println("    - GET  http://localhost:" + PORT + "/api/instruments");
        System.out.println("    - GET  http://localhost:" + PORT + "/api/portfolio/valuation?rate=0.05");
        System.out.println("    - POST http://localhost:" + PORT + "/api/instruments/import-xml");
        System.out.println("    - GET  http://localhost:" + PORT + "/health");
        System.out.println("===============================================================");

        server.start();
    }
}
