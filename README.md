# 🏦 Fintech Financial Instruments & Risk Valuation Platform

[![Java](https://img.shields.io/badge/Java-8%2B-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Gradle](https://img.shields.io/badge/Build-Gradle-02303A?logo=gradle&logoColor=white)](https://gradle.org/)
[![JUnit 5](https://img.shields.io/badge/Tests-JUnit_5_(6%2F6_Passing)-25A162?logo=junit5&logoColor=white)](./tests)
[![SQL](https://img.shields.io/badge/Database-PostgreSQL_%2F_SQL-336791?logo=postgresql&logoColor=white)](./sql)
[![Live Demo](https://img.shields.io/badge/Live_Demo-GitHub_Pages-0ea5e9?logo=github)](https://juancovili.github.io/fintech-instrument-service/)

> Plataforma backend de servicios financieros (*Front to Back to Risk*) para **valorización de instrumentos de Renta Fija (Bonos: Valor Presente, TIR/YTM y Duración de Macaulay) y Renta Variable**, ingesta segura de feeds de mercado en **XML (W3C DOM)**, persistencia relacional en **SQL** y exposición mediante **API REST** en **Java 8+** aplicando **Patrones de Diseño GoF**.

🔗 **[Probar Simulador Interactivo en Vivo (GitHub Pages)](https://juancovili.github.io/fintech-instrument-service/)**

![Vista Previa del Motor Financiero](./docs/preview.svg)

---

## 🏛️ Arquitectura del Sistema & Patrones de Diseño (GoF)

El motor desacopla las reglas matemáticas de mercado del transporte HTTP y la persistencia SQL mediante cuatro patrones de diseño clásicos:

```mermaid
flowchart TD
    subgraph ClientLayer ["Capa de Cliente & Feeds de Mercado"]
        A["Feed XML de Instrumentos (FIX / Bancos)"] -->|HTTP POST /api/instruments/import-xml| E["FintechRestServer (HttpServer)"]
        B["Simulador Web / Clientes REST"] -->|HTTP GET /api/portfolio/valuation| E
    end

    subgraph CoreEngine ["Motor Central Java 8+ (Patrones GoF)"]
        E --> G["XmlInstrumentParser (W3C DOM + Protección XXE)"]
        G --> H["InstrumentFactory (Factory Method)"]
        H --> I["InstrumentRepository (Singleton + DAO)"]
        E --> J["ValuationService (Contexto Strategy)"]
        J -->|Renta Fija: DCF + Duración Macaulay| K["BondValuationStrategy"]
        J -->|Renta Variable: Mark-to-Market| L["EquityValuationStrategy"]
        I -.-> J
    end

    subgraph PersistenceLayer ["Capa de Datos Relacional (SQL 3FN)"]
        I <--> M[("PostgreSQL / SQLite\n01_schema.sql · 03_risk_queries.sql")]
    end
```

### Diagrama de Clases (Polimorfismo & Strategy Pattern)

```mermaid
classDiagram
    class FinancialInstrument {
        <<abstract>>
        +String isin
        +String ticker
        +String issuer
        +String currency
        +getInstrumentType()* String
    }
    class BondInstrument {
        +double faceValue
        +double couponRate
        +int maturityYears
        +String creditRating
    }
    class EquityInstrument {
        +double marketPrice
        +int shares
        +double dividendYield
        +String sector
    }
    class ValuationStrategy~T~ {
        <<interface>>
        +calculatePresentValue(T instrument, double discountRate) double
    }
    class BondValuationStrategy {
        +calculatePresentValue(BondInstrument bond, double rate) double
        +calculateMacaulayDuration(BondInstrument bond, double rate) double
    }
    class EquityValuationStrategy {
        +calculatePresentValue(EquityInstrument equity, double rate) double
    }

    FinancialInstrument <|-- BondInstrument
    FinancialInstrument <|-- EquityInstrument
    ValuationStrategy <|.. BondValuationStrategy
    ValuationStrategy <|.. EquityValuationStrategy
```

---

## 🧪 Suite de Pruebas Automatizadas ([`/tests`](./tests))

El proyecto incluye una suite de pruebas automatizadas en **JUnit 5** (disponible en [`/tests/FintechEngineTestSuite.java`](./tests/FintechEngineTestSuite.java) y `src/test/java/`):

| # | Caso de Prueba (`@Test`) | Validación Financiera / Técnica | Estado |
| :--- | :--- | :--- | :---: |
| **1** | `testBondAtParValuation` | Bono a la par: cuando `tasa cupón (5%) == tasa de descuento (5%)`, el Valor Presente es exactamente igual al Nominal (`1000.00`). | ✅ PASS |
| **2** | `testBondInverseInterestRateRelationship` | Relación inversa precio/tasa: verifica cotización **Sobre la Par (Con Prima)** ante caída de tasas y **Bajo la Par (Con Descuento)** ante alza de tasas. | ✅ PASS |
| **3** | `testMacaulayDurationBounds` | Verifica el cálculo de **Duración de Macaulay** ponderada por flujos descontados ($0 < D < T$). | ✅ PASS |
| **4** | `testEquityValuationStrategy` | Verifica valorización *Mark-to-Market* de posiciones accionarias con el **Patrón Strategy**. | ✅ PASS |
| **5** | `testValidXmlIngestion` | Verifica el parseo W3C DOM de carteras XML y creación polimórfica con `InstrumentFactory`. | ✅ PASS |
| **6** | `testMalformedXmlThrowsControlledException` | Verifica rechazo seguro de XML corrupto y bloqueo de entidades externas (`XXE`). | ✅ PASS |

```bash
# Ejecutar todas las pruebas unitarias
./gradlew test
```

---

## 🗄️ Modelo Relacional SQL & Consultas de Riesgo ([`/sql`](./sql))

* **[`sql/01_schema.sql`](./sql/01_schema.sql):** Tablas normalizadas (`portfolios`, `financial_instruments`, `portfolio_positions`, `market_prices`) con claves foráneas e índices.
* **[`sql/02_seed_data.sql`](./sql/02_seed_data.sql):** Datos semilla con bonos soberanos y corporativos (`BCU-10Y`, `UST-10Y`, `Banco de Chile`, `SQM`).
* **[`sql/03_risk_queries.sql`](./sql/03_risk_queries.sql):** Consultas analíticas con `INNER JOIN`, `GROUP BY` y funciones de agregación para **Mark-to-Market (MTM)**, **PnL no realizado** y **concentración de exposición por emisor**.

---

## 🚀 Ejecución Local y con Docker

```bash
# Opción 1: Ejecutar con Gradle (Java 8+)
./gradlew run

# Opción 2: Construir y ejecutar con Docker Multi-Stage
docker build -t fintech-instrument-service .
docker run -p 8080:8080 fintech-instrument-service
```

---

## 👨‍💻 Autor

**Juan Covili**  
*Ingeniero en Informática / Analista Programador (INACAP)*  
* [LinkedIn](https://www.linkedin.com/in/juan-covili-59b935241/) · [GitHub](https://github.com/juanCovili) · [Portafolio Web](https://www.juancovili.com)
