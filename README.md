# EFD Fiscal Parser

Java 17 library + CLI for parsing **SPED EFD Fiscal** (EFD ICMS/IPI) flat-text files and generating a JSON summary of ICMS apuration per competency period.

---

## What is EFD Fiscal?

EFD Fiscal (Escrituração Fiscal Digital) is the Brazilian electronic tax bookkeeping format mandated by SEFAZ. Companies transmit monthly files containing all fiscal operations, which feed ICMS/IPI apuration.

Each file is a plain text file encoded in **ISO-8859-1**, where every line follows the pattern:

```
|REG|CAMPO1|CAMPO2|...|CAMPO_N|
```

- The `REG` code identifies the record type (e.g., `C100`, `E110`).
- Fields are separated by `|` and may be empty.
- Decimal values use **Brazilian format**: period as thousands separator, comma as decimal (`1.200,50` = 1200.50).
- Dates are in `DDMMAAAA` format (`01012024` = 01/01/2024).

---

## Supported Records

| Record | Block | Description |
|--------|-------|-------------|
| `0000` | 0     | File header — company identification, period |
| `C100` | C     | Fiscal document (NF-e model 55, NFC-e model 65) |
| `C170` | C     | Items of the fiscal document |
| `E110` | E     | ICMS apuration totals (authoritative) |
| `E111` | E     | ICMS adjustment entries (e.g., outorgado credit) |

All other records (`C190`, `9900`, `9999`, etc.) are silently skipped. Unknown records generate a FINE-level log message; malformed lines generate a WARNING and increment the `warnings` counter in the output.

---

## Building

Requires **Java 17+** and **Maven 3.8+**.

```bash
mvn package
```

This produces `target/efd-parser.jar` — a fat JAR with Jackson bundled.

Run tests:

```bash
mvn test
```

---

## CLI Usage

```bash
java -jar target/efd-parser.jar \
  --input  sample/efd_fiscal_sample.txt \
  --output summary.json \
  --period 2024-01
```

| Flag       | Required | Description |
|------------|----------|-------------|
| `--input`  | Yes      | Path to the EFD Fiscal text file |
| `--output` | No       | Output JSON path (default: stdout) |
| `--period` | No       | Competency period `YYYY-MM` for validation |

If `--period` is supplied and does not match the file header period, a warning is printed and parsing continues.

---

## Sample Input

See [`sample/efd_fiscal_sample.txt`](sample/efd_fiscal_sample.txt) for a complete, realistic example with:

- 3 outgoing NF-e documents (saídas) totalling **R$ 2.760,00** ICMS
- 2 incoming NF-e documents (entradas) totalling **R$ 1.800,00** ICMS credit
- 1 credit adjustment (E111) of **R$ 100,00** (crédito outorgado SP)
- E110 apuration: **R$ 860,00** ICMS a recolher

Excerpt:

```
|0000|011|0|01012024|31012024|EMPRESA COMERCIAL LTDA|12345678000190||SP|123456789|3550308|98765||A|1|
|C100|1|0|CLIENTE001|55|00|001|000001|...|01012024|01012024|12000,00|...|10000,00|1200,00|...|
|C170|1|PROD001|Produto A - Cabos USB|10,000|UN|1000,00|0,00|0|000|5101||10000,00|12,00|1200,00|...|
|E110|2760,00|0,00|2760,00|0,00|1800,00|100,00|1900,00|0,00|0,00|860,00|0,00|860,00|0,00|0,00|
|E111|SP10000001|Credito outorgado - supermercado|100,00|
```

---

## Sample Output

```json
{
  "period": "2024-01",
  "company": "EMPRESA COMERCIAL LTDA",
  "cnpj": "12345678000190",
  "uf": "SP",
  "startDate": "01/01/2024",
  "endDate": "31/01/2024",
  "apuration": {
    "totalDebits": 2760.00,
    "debitAdjustments": 0.00,
    "totalWithDebitAdjustments": 2760.00,
    "creditReversals": 0.00,
    "totalCredits": 1800.00,
    "creditAdjustments": 100.00,
    "totalWithCreditAdjustments": 1900.00,
    "debitReversals": 0.00,
    "previousCreditBalance": 0.00,
    "calculatedBalance": 860.00,
    "totalDeductions": 0.00,
    "icmsToCollect": 860.00,
    "creditToCarryForward": 0.00,
    "specialDebits": 0.00
  },
  "adjustments": [
    {
      "code": "SP10000001",
      "description": "Credito outorgado - supermercado",
      "value": 100.00
    }
  ],
  "documentCount": 5,
  "itemCount": 5,
  "documents": [
    {
      "documentNumber": "000001",
      "series": "001",
      "operationType": "SAIDA",
      "documentDate": "01/01/2024",
      "nfeKey": "35240112345678000190550010000001234567890000",
      "totalValue": 12000.00,
      "icmsBase": 10000.00,
      "icmsValue": 1200.00,
      "items": [
        {
          "itemNumber": 1,
          "itemCode": "PROD001",
          "description": "Produto A - Cabos USB",
          "quantity": 10.000,
          "unit": "UN",
          "unitValue": 1000.00,
          "cfop": "5101",
          "icmsCst": "000",
          "icmsBase": 10000.00,
          "icmsRate": 12.00,
          "icmsValue": 1200.00
        }
      ]
    }
  ],
  "warnings": 0
}
```

---

## Architecture

```
br.com.efdparser
├── Main.java                  CLI entry point
├── EfdParser.java             File reader + orchestrator
├── cli/
│   └── CliOptions.java        Argument parsing (no external lib)
├── model/
│   ├── Record0000.java        Java record — file header
│   ├── RecordC100.java        Java record — fiscal document
│   ├── RecordC170.java        Java record — document item
│   ├── RecordE110.java        Java record — ICMS apuration totals
│   ├── RecordE111.java        Java record — ICMS adjustment
│   └── EfdSummary.java        Output DTO serialized to JSON
└── parser/
    ├── BaseParser.java        Field access + validation helpers
    ├── Record0000Parser.java
    ├── RecordC100Parser.java
    ├── RecordC170Parser.java
    ├── RecordE110Parser.java
    └── RecordE111Parser.java
```

**Dependencies:** Jackson Databind 2.16.1 (JSON), JUnit Jupiter 5.10.1 (tests only).

---

## Using as a Library

```java
EfdParser parser = new EfdParser();
EfdSummary summary = parser.parse(Path.of("arquivo.txt"), "2024-01");

System.out.println(summary.apuration.icmsToCollect); // e.g., 860.00
summary.documents.forEach(doc ->
    System.out.printf("NF %s — ICMS R$ %.2f%n", doc.documentNumber, doc.icmsValue));
```

---

## Reforma Tributária

With Brazil's tax reform (LC 214/2024) replacing ICMS with CBS/IBS, this parser serves as a reference for legacy EFD ICMS apuration logic — understanding the current saldo/débito/crédito model is essential for designing the transition rules and credit transfer mechanisms.
