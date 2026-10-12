package com.juancovili.fintech.controller;

import com.juancovili.fintech.model.BondInstrument;
import com.juancovili.fintech.model.EquityInstrument;
import com.juancovili.fintech.model.FinancialInstrument;
import com.juancovili.fintech.parser.XmlInstrumentParser;
import com.juancovili.fintech.repository.InstrumentRepository;
import com.juancovili.fintech.service.BondValuationStrategy;
import com.juancovili.fintech.service.EquityValuationStrategy;
import com.juancovili.fintech.service.ValuationStrategy;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lightweight REST Controller implementing RESTful web service endpoints.
 */
public class InstrumentRestHandler implements HttpHandler {

    private final InstrumentRepository repository = InstrumentRepository.getInstance();
    private final ValuationStrategy bondStrategy = new BondValuationStrategy();
    private final ValuationStrategy equityStrategy = new EquityValuationStrategy();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Enable CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(method) && "/api/instruments".equals(path)) {
                handleGetInstruments(exchange);
            } else if ("GET".equalsIgnoreCase(method) && "/api/portfolio/valuation".equals(path)) {
                handleGetValuation(exchange);
            } else if ("POST".equalsIgnoreCase(method) && "/api/instruments/import-xml".equals(path)) {
                handleImportXml(exchange);
            } else if ("GET".equalsIgnoreCase(method) && "/health".equals(path)) {
                sendJsonResponse(exchange, 200, "{\"status\":\"UP\",\"engine\":\"Fintech Valuation Core\",\"javaVersion\":\"8+\"}");
            } else {
                sendJsonResponse(exchange, 404, "{\"error\":\"Not Found\"}");
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleGetInstruments(HttpExchange exchange) throws IOException {
        List<FinancialInstrument> instruments = repository.findAll();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < instruments.size(); i++) {
            FinancialInstrument inst = instruments.get(i);
            json.append(formatInstrumentJson(inst));
            if (i < instruments.size() - 1) json.append(",");
        }
        json.append("]");
        sendJsonResponse(exchange, 200, json.toString());
    }

    private void handleGetValuation(HttpExchange exchange) throws IOException {
        double discountRate = 0.05; // 5% default market risk-free rate
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("rate=")) {
            try {
                discountRate = Double.parseDouble(query.split("rate=")[1].split("&")[0]);
            } catch (Exception ignored) {}
        }

        double totalFairValue = 0.0;
        List<FinancialInstrument> instruments = repository.findAll();
        StringBuilder json = new StringBuilder("{\"discountRate\":").append(discountRate)
                .append(",\"totalBookValue\":").append(repository.calculateTotalPortfolioValue())
                .append(",\"instruments\":[");

        for (int i = 0; i < instruments.size(); i++) {
            FinancialInstrument inst = instruments.get(i);
            double fairVal = 0.0;
            if (inst instanceof BondInstrument) {
                fairVal = bondStrategy.calculateFairValue(inst, discountRate);
            } else if (inst instanceof EquityInstrument) {
                fairVal = equityStrategy.calculateFairValue(inst, discountRate);
            }
            totalFairValue += fairVal;

            json.append("{\"isin\":\"").append(inst.getIsin())
                .append("\",\"ticker\":\"").append(inst.getTicker())
                .append("\",\"type\":\"").append(inst.getType())
                .append("\",\"fairValue\":").append(String.format("%.2f", fairVal))
                .append("}");
            if (i < instruments.size() - 1) json.append(",");
        }

        json.append("],\"totalFairValue\":").append(String.format("%.2f", totalFairValue)).append("}");
        sendJsonResponse(exchange, 200, json.toString());
    }

    private void handleImportXml(HttpExchange exchange) throws Exception {
        String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        List<FinancialInstrument> imported = XmlInstrumentParser.parseXml(body);
        repository.saveAll(imported);

        String response = "{\"status\":\"SUCCESS\",\"importedCount\":" + imported.size() + "}";
        sendJsonResponse(exchange, 201, response);
    }

    private String formatInstrumentJson(FinancialInstrument inst) {
        return "{\"isin\":\"" + inst.getIsin() + "\""
                + ",\"ticker\":\"" + inst.getTicker() + "\""
                + ",\"issuer\":\"" + inst.getIssuer() + "\""
                + ",\"type\":\"" + inst.getType() + "\""
                + ",\"quantity\":" + inst.getQuantity()
                + ",\"marketPrice\":" + inst.getMarketPrice()
                + ",\"bookValue\":" + inst.calculateBookValue() + "}";
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
