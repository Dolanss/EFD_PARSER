package br.com.efdparser.parser;

import br.com.efdparser.model.RecordE110;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RecordE110ParserTest {

    private final RecordE110Parser parser = new RecordE110Parser();

    // Scenario: debits=2760, credits=1800, credit adjustment=100 → balance=860 to collect
    private static final String VALID_LINE =
        "|E110|2760,00|0,00|2760,00|0,00|1800,00|100,00|1900,00|0,00|0,00|860,00|0,00|860,00|0,00|0,00|";

    @Test
    void parsesAllFields() {
        RecordE110 r = parser.parse(VALID_LINE);

        assertEquals(new BigDecimal("2760.00"), r.totalDebits());
        assertEquals(BigDecimal.ZERO, r.debitAdjustments());
        assertEquals(new BigDecimal("2760.00"), r.totalWithDebitAdjustments());
        assertEquals(BigDecimal.ZERO, r.creditReversals());
        assertEquals(new BigDecimal("1800.00"), r.totalCredits());
        assertEquals(new BigDecimal("100.00"), r.creditAdjustments());
        assertEquals(new BigDecimal("1900.00"), r.totalWithCreditAdjustments());
        assertEquals(BigDecimal.ZERO, r.debitReversals());
        assertEquals(BigDecimal.ZERO, r.previousCreditBalance());
        assertEquals(new BigDecimal("860.00"), r.calculatedBalance());
        assertEquals(BigDecimal.ZERO, r.totalDeductions());
        assertEquals(new BigDecimal("860.00"), r.icmsToCollect());
        assertEquals(BigDecimal.ZERO, r.creditToCarryForward());
        assertEquals(BigDecimal.ZERO, r.specialDebits());
    }

    @Test
    void parsesCreditBalanceScenario() {
        // When credits exceed debits → credit to carry forward
        String line = "|E110|1000,00|0,00|1000,00|0,00|2000,00|0,00|2000,00|0,00|0,00|0,00|0,00|0,00|1000,00|0,00|";
        RecordE110 r = parser.parse(line);

        assertEquals(new BigDecimal("1000.00"), r.totalDebits());
        assertEquals(new BigDecimal("2000.00"), r.totalCredits());
        assertEquals(BigDecimal.ZERO, r.icmsToCollect());
        assertEquals(new BigDecimal("1000.00"), r.creditToCarryForward());
    }

    @Test
    void parsesThousandSeparators() {
        String line = "|E110|1.200,00|0,00|1.200,00|0,00|800,00|0,00|800,00|0,00|0,00|400,00|0,00|400,00|0,00|0,00|";
        RecordE110 r = parser.parse(line);

        assertEquals(new BigDecimal("1200.00"), r.totalDebits());
        assertEquals(new BigDecimal("400.00"), r.icmsToCollect());
    }

    @Test
    void treatsEmptyFieldsAsZero() {
        String line = "|E110||0,00||0,00||0,00||0,00|0,00|0,00|0,00|0,00|0,00|0,00|";
        RecordE110 r = parser.parse(line);
        assertEquals(BigDecimal.ZERO, r.totalDebits());
        assertEquals(BigDecimal.ZERO, r.icmsToCollect());
    }

    @Test
    void rejectsWrongRegCode() {
        String line = "|E111|SP10000001|Ajuste|100,00|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void rejectsTooFewFields() {
        String line = "|E110|2760,00|0,00|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }
}
