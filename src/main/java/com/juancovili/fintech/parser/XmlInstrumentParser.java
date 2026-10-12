package com.juancovili.fintech.parser;

import com.juancovili.fintech.model.FinancialInstrument;
import com.juancovili.fintech.service.InstrumentFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard XML Parser using Java DOM APIs (javax.xml.parsers).
 */
public class XmlInstrumentParser {

    public static List<FinancialInstrument> parseXml(String xmlContent) throws Exception {
        return parseXml(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
    }

    public static List<FinancialInstrument> parseXml(InputStream inputStream) throws Exception {
        List<FinancialInstrument> instruments = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        doc.getDocumentElement().normalize();

        NodeList nodes = doc.getElementsByTagName("instrument");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String type = el.getAttribute("type").toUpperCase();
            String isin = getTagValue(el, "isin");
            String ticker = getTagValue(el, "ticker");
            String issuer = getTagValue(el, "issuer");
            double quantity = Double.parseDouble(getTagValue(el, "quantity"));
            double marketPrice = Double.parseDouble(getTagValue(el, "marketPrice"));

            if ("BOND".equals(type)) {
                double nominalValue = Double.parseDouble(getTagValue(el, "nominalValue"));
                double couponRate = Double.parseDouble(getTagValue(el, "couponRate"));
                int maturityYears = Integer.parseInt(getTagValue(el, "maturityYears"));
                instruments.add(InstrumentFactory.createBond(isin, ticker, issuer, quantity, marketPrice, nominalValue, couponRate, maturityYears));
            } else if ("EQUITY".equals(type)) {
                double dividendYield = Double.parseDouble(getTagValue(el, "dividendYield"));
                instruments.add(InstrumentFactory.createEquity(isin, ticker, issuer, quantity, marketPrice, dividendYield));
            }
        }
        return instruments;
    }

    private static String getTagValue(Element element, String tagName) {
        NodeList nl = element.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            return nl.item(0).getTextContent().trim();
        }
        return "";
    }
}
