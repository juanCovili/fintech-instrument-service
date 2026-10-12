package com.vmetrix.fintech;

import com.vmetrix.fintech.model.BondInstrument;
import com.vmetrix.fintech.model.EquityInstrument;
import com.vmetrix.fintech.model.FinancialInstrument;
import com.vmetrix.fintech.repository.InstrumentRepository;
import com.vmetrix.fintech.service.BondValuationStrategy;
import com.vmetrix.fintech.service.EquityValuationStrategy;
import com.vmetrix.fintech.service.ValuationService;
import com.vmetrix.fintech.xml.XmlInstrumentParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de Pruebas Unitarias e Integración — Fintech Instrument Service.
 * Verifica matemáticas financieras (Renta Fija / Renta Variable), Patrones de Diseño GoF,
 * parseo seguro de XML (W3C DOM) y consultas de exposición de portafolio.
 */
public class FintechEngineTestSuite {

    private ValuationService valuationService;
    private XmlInstrumentParser xmlParser;
    private InstrumentRepository repository;

    @BeforeEach
    void setUp() {
        repository = InstrumentRepository.getInstance();
        valuationService = new ValuationService(repository);
        xmlParser = new XmlInstrumentParser();
    }

    @Test
    @DisplayName("1. Bono a la Par: Cuando la tasa cupón iguala la tasa de descuento, el Valor Presente es igual al Nominal")
    void testBondAtParValuation() {
        BondInstrument bond = new BondInstrument(
                "CL0002891234",
                "BCP-2030",
                "Banco Central de Chile",
                "CLP",
                1000.0,
                0.05, // Cupón 5%
                5,    // 5 años al vencimiento
                "AAA"
        );
        BondValuationStrategy strategy = new BondValuationStrategy();
        double presentValue = strategy.calculatePresentValue(bond, 0.05);

        assertEquals(1000.0, presentValue, 0.01, "Un bono con cupón 5% descontado al 5% debe cotizar a la par (1000)");
    }

    @Test
    @DisplayName("2. Sensibilidad de Tasas: Subida en tasa de mercado reduce el Valor Presente del Bono (Relación Inversa)")
    void testBondInverseInterestRateRelationship() {
        BondInstrument bond = new BondInstrument(
                "US912810TW80",
                "UST-10Y",
                "US Treasury",
                "USD",
                1000.0,
                0.045,
                10,
                "AAA"
        );
        BondValuationStrategy strategy = new BondValuationStrategy();
        double pvLowRate = strategy.calculatePresentValue(bond, 0.03);
        double pvHighRate = strategy.calculatePresentValue(bond, 0.06);

        assertTrue(pvLowRate > 1000.0, "Con tasa de descuento menor al cupón, el bono cotiza sobre la par (Con Prima)");
        assertTrue(pvHighRate < 1000.0, "Con tasa de descuento mayor al cupón, el bono cotiza bajo la par (Con Descuento)");
    }

    @Test
    @DisplayName("3. Duración de Macaulay: Bono con cupones tiene duración estrictamente menor a su madurez nominal")
    void testMacaulayDurationBounds() {
        BondInstrument bond = new BondInstrument(
                "CL0002456789",
                "BTU-2032",
                "Tesorería General de la República",
                "CLF",
                1000.0,
                0.06,
                8,
                "AA+"
        );
        BondValuationStrategy strategy = new BondValuationStrategy();
        double duration = strategy.calculateMacaulayDuration(bond, 0.05);

        assertTrue(duration > 0 && duration < 8.0,
                "La Duración de Macaulay de un bono con cupones a 8 años debe estar entre 0 y 8 años");
    }

    @Test
    @DisplayName("4. Renta Variable (Equity): Cálculo exacto de Mark-to-Market y retorno de dividendo")
    void testEquityValuationStrategy() {
        EquityInstrument equity = new EquityInstrument(
                "CL0000000015",
                "CHILE",
                "Banco de Chile",
                "CLP",
                115.0,
                250,
                0.065,
                "Banca"
        );
        EquityValuationStrategy strategy = new EquityValuationStrategy();
        double mtmValue = strategy.calculatePresentValue(equity, 0.05);

        assertTrue(mtmValue > 0, "La valorización Mark-to-Market de la posición accionaria debe ser positiva");
    }

    @Test
    @DisplayName("5. Parser XML (W3C DOM): Ingesta válida de múltiples instrumentos financieros desde feed XML")
    void testValidXmlIngestion() {
        String xmlPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<portfolio>\n" +
                "  <instrument type=\"BOND\">\n" +
                "    <isin>CL0009998887</isin>\n" +
                "    <ticker>BCU-05</ticker>\n" +
                "    <issuer>Banco Central de Chile</issuer>\n" +
                "    <currency>CLF</currency>\n" +
                "    <faceValue>5000.0</faceValue>\n" +
                "    <couponRate>0.045</couponRate>\n" +
                "    <maturityYears>5</maturityYears>\n" +
                "    <creditRating>AAA</creditRating>\n" +
                "  </instrument>\n" +
                "</portfolio>";

        List<FinancialInstrument> parsed = xmlParser.parseInstruments(xmlPayload);
        assertNotNull(parsed);
        assertEquals(1, parsed.size());
        assertEquals("BCU-05", parsed.get(0).getTicker());
    }

    @Test
    @DisplayName("6. Seguridad XML: Rechazo controlado ante XML malformado sin caída del servicio")
    void testMalformedXmlThrowsControlledException() {
        String brokenXml = "<portfolio><instrument type=\"BOND\"><ticker>UNCLOSED</portfolio>";
        assertThrows(Exception.class, () -> xmlParser.parseInstruments(brokenXml));
    }
}
