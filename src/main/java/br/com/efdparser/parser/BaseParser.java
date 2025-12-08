package br.com.efdparser.parser;

import java.math.BigDecimal;

/**
 * Common field-access helpers for EFD record parsers.
 * EFD lines are pipe-delimited: |REG|FIELD1|FIELD2|...|
 * After stripping the outer pipes and splitting, fields[0] is the REG code.
 * Brazilian decimal format uses comma as separator: "1.200,50" → 1200.50
 */
abstract class BaseParser {

    /**
     * Splits a raw EFD line into a fields array where fields[0] = REG code.
     * Validates that the line starts and ends with '|'.
     */
    static String[] splitFields(String line) {
        if (line == null || line.length() < 2) {
            throw new IllegalArgumentException("Line too short to be a valid EFD record: '" + line + "'");
        }
        if (!line.startsWith("|")) {
            throw new IllegalArgumentException("EFD line must start with '|': '" + line + "'");
        }
        if (!line.endsWith("|")) {
            throw new IllegalArgumentException("EFD line must end with '|': '" + line + "'");
        }
        return line.substring(1, line.length() - 1).split("\\|", -1);
    }

    /** Returns trimmed field value, or empty string when index is out of bounds. */
    protected String str(String[] fields, int index) {
        return index < fields.length ? fields[index].trim() : "";
    }

    /**
     * Parses a Brazilian-formatted decimal string (e.g. "1.200,50") to BigDecimal.
     * Returns BigDecimal.ZERO for blank or absent fields, and also for any zero-valued
     * field (e.g. "0,00") so that callers can compare safely with BigDecimal.ZERO
     * without worrying about scale differences.
     */
    protected BigDecimal dec(String[] fields, int index) {
        var raw = str(fields, index);
        if (raw.isEmpty()) return BigDecimal.ZERO;
        try {
            // Remove thousands separator (period), replace decimal separator (comma) with dot
            var result = new BigDecimal(raw.replace(".", "").replace(",", "."));
            // Normalize zero: BigDecimal.equals() is scale-sensitive, so "0,00" (scale 2)
            // would not equal BigDecimal.ZERO (scale 0) without this normalization.
            return result.signum() == 0 ? BigDecimal.ZERO : result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Cannot parse decimal value '" + raw + "' at field index " + index);
        }
    }

    /** Parses an integer field; returns 0 for blank or absent fields. */
    protected int integer(String[] fields, int index) {
        var raw = str(fields, index);
        if (raw.isEmpty()) return 0;
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Cannot parse integer value '" + raw + "' at field index " + index);
        }
    }

    /** Validates that the REG code matches the expected value. */
    protected void requireReg(String[] fields, String expected) {
        var actual = str(fields, 0);
        if (!expected.equals(actual)) {
            throw new IllegalArgumentException(
                "Expected REG=" + expected + " but got REG=" + actual);
        }
    }

    /** Validates minimum field count. */
    protected void requireMinFields(String[] fields, int min, String reg) {
        if (fields.length < min) {
            throw new IllegalArgumentException(
                "Record " + reg + " requires at least " + min + " fields, got " + fields.length);
        }
    }
}
