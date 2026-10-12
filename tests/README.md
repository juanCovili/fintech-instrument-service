# 🧪 Suite de Pruebas Automatizadas (`JUnit 5`)

Este directorio contiene las pruebas unitarias y de integración del motor financiero (**Fintech Instrument Service**), verificando tanto la exactitud matemática de la valorización de instrumentos como la seguridad en la ingesta de archivos XML.

---

## 📋 Casos de Prueba Cubiertos

| # | Prueba (`@Test`) | Qué valida | Resultado Esperado |
| :--- | :--- | :--- | :--- |
| **1** | `testBondAtParValuation` | Valorización por Flujos Descontados (DCF) cuando la tasa cupón ($5\%$) iguala la tasa de descuento ($5\%$). | `PV == Nominal (1000.00)` |
| **2** | `testBondInverseInterestRateRelationship` | Relación inversa entre tasa de interés de mercado y precio del bono (sobre la par vs. bajo la par). | `PV(3%) > 1000 > PV(6%)` |
| **3** | `testMacaulayDurationBounds` | Cálculo de sensibilidad temporal (**Duración de Macaulay**) para un bono con cupones a 8 años. | `0 < Duración < 8.0 años` |
| **4** | `testEquityValuationStrategy` | Polimorfismo del **Patrón Strategy** aplicado a instrumentos de Renta Variable (*Mark-to-Market*). | `MTM > 0` |
| **5** | `testValidXmlIngestion` | Parseo DOM (`javax.xml.parsers.DocumentBuilder`) e instanciación mediante **Patrón Factory**. | Creación de `BondInstrument` (`BCU-05`) |
| **6** | `testMalformedXmlThrowsControlledException` | Manejo defensivo de errores de sintaxis XML y prevención de inyección de entidades externas (`XXE`). | Excepción controlada sin caída del servidor |

---

## ▶️ Cómo Ejecutar las Pruebas

```bash
# Ejecutar toda la suite con Gradle
./gradlew test

# Salida esperada:
# > Task :test
# FintechEngineTestSuite > 1. Bono a la Par PASSED
# FintechEngineTestSuite > 2. Sensibilidad de Tasas PASSED
# FintechEngineTestSuite > 3. Duración de Macaulay PASSED
# FintechEngineTestSuite > 4. Renta Variable (Equity) PASSED
# FintechEngineTestSuite > 5. Parser XML (W3C DOM) PASSED
# FintechEngineTestSuite > 6. Seguridad XML PASSED
# BUILD SUCCESSFUL
```
