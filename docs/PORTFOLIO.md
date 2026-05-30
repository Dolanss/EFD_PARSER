# Portfolio Positioning

## Short GitHub Description

Java 17 CLI and library for parsing SPED EFD Fiscal flat files and generating normalized JSON summaries of ICMS apuration.

## LinkedIn Description

Built a Java 17 parser for Brazilian SPED EFD Fiscal files, focused on fiscal data ingestion, ICMS apuration summaries, safe monetary parsing with BigDecimal, and testable record-level parsing for real-world flat-file processing.

## 30-Second Pitch

This project parses SPED EFD Fiscal files, which are Brazilian fiscal bookkeeping files transmitted monthly by companies. The parser reads the file line by line, identifies supported record types, extracts company metadata, fiscal documents, item details, ICMS apuration totals, and adjustment entries, then produces a normalized JSON summary. The main engineering focus is reliable flat-file ingestion: legacy encoding, positional fields, Brazilian decimal formats, recoverable parsing errors, and clear separation between parser orchestration and record-specific mapping.

## How to Explain It to Different Audiences

### Recruiter

It is a backend data-processing project that handles a real fiscal file format used by Brazilian companies. It shows Java, testing, file processing, JSON generation, and attention to financial data correctness.

### Technical Recruiter

It is a Java 17 CLI/library that parses SPED EFD Fiscal flat files and extracts structured ICMS apuration data. It demonstrates domain modeling, parser design, BigDecimal usage, tests, build automation, and GitHub Actions CI.

### Tech Lead

The design separates file orchestration from record-specific parsers, treats `E110` as the authoritative apuration source, uses BigDecimal for fiscal values, and handles malformed input as recoverable warnings where possible. The current scope is intentionally narrow, with clear extension points for layout validation, block-control reconciliation, streaming output, and persistence.

### Senior Engineer

The interesting part is not the syntax of parsing delimiters, but the operational shape of a fiscal ingestion pipeline: versioned layouts, partial failures, data-quality reporting, sensitive identifiers, idempotent reprocessing, reconciliation against control records, memory pressure from large files, and traceability of derived outputs.

## Strong Resume Bullet

Implemented a Java 17 SPED EFD Fiscal parser that reads legacy encoded flat files, maps fiscal records into typed domain models, extracts ICMS apuration data, and generates JSON summaries with unit and integration test coverage.

## Stronger Resume Bullet With Production Framing

Designed and implemented a Java 17 fiscal data ingestion tool for SPED EFD Fiscal files, using record-level parsers, BigDecimal-based monetary parsing, recoverable error handling, and CI-backed tests to normalize ICMS apuration data into JSON for downstream analysis.

## Technical Strengths to Highlight

- Real-world file ingestion instead of generic CRUD.
- Fiscal domain modeling with explicit record classes.
- Correct money handling with BigDecimal.
- Legacy charset handling with ISO-8859-1.
- Recoverable parsing failures with warning counters.
- Unit and integration tests around parser behavior.
- Small dependency footprint.
- Clear roadmap toward production readiness.

## Claims to Avoid

- Do not call it a complete tax engine.
- Do not claim it validates every SPED EFD record.
- Do not claim it is cloud-native.
- Do not claim it handles unlimited file sizes.
- Do not claim it calculates ICMS from scratch.

## Defensible Production Evolution

1. Add `C190` parsing and reconciliation against `E110`.
2. Validate block 9 control records.
3. Add layout-version support based on `COD_VER`.
4. Add structured logs and metrics.
5. Add streaming JSON output for large files.
6. Add Docker packaging.
7. Add batch processing with idempotency by file hash.
8. Add persistence for parsed records and processing audit.

## Realistic Commit Roadmap

- `chore: initialize Java 17 Maven project`
  - Add `pom.xml`, package structure, Maven plugins.
- `feat: add EFD line parsing helpers`
  - Add delimiter validation, decimal parsing, integer parsing.
- `feat: parse EFD header and fiscal document records`
  - Add `0000`, `C100`, and related tests.
- `feat: associate fiscal document items with C100 records`
  - Add `C170` parser and document-item grouping.
- `feat: extract ICMS apuration and adjustment records`
  - Add `E110`, `E111`, and summary mapping.
- `test: add integration coverage for sample EFD file`
  - Add end-to-end parser assertions.
- `docs: document architecture and production trade-offs`
  - Improve README with diagrams and operational notes.
- `ci: add GitHub Actions build and test workflow`
  - Run Maven tests and package build on push/PR.
- `refactor: isolate record-specific parsing behavior`
  - Keep orchestration thin and parsers focused.
- `feat: add block-control validation`
  - Future improvement for `9900`, `9990`, `9999`.
