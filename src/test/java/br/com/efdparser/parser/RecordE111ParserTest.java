package br.com.efdparser.parser;

import br.com.efdparser.model.RecordE111;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RecordE111ParserTest {

    private final RecordE111Parser parser = new RecordE111Parser();

    @Test
    void parsesStandardAdjustment() {
        String line = "|E111|SP10000001|Credito outorgado - supermercado|100,00|";
        RecordE111 r = parser.parse(line);

        assertEquals("SP10000001", r.adjustmentCode());
        assertEquals("Credito outorgado - supermercado", r.description());
        assertEquals(new BigDecimal("100.00"), r.value());
    }

    @Test
    void parsesLargeValue() {
        String line = "|E111|MG10000123|Estorno de debito por devolucao|1.500,75|";
        RecordE111 r = parser.parse(line);

        assertEquals("MG10000123", r.adjustmentCode());
        assertEquals(new BigDecimal("1500.75"), r.value());
    }

    @Test
    void parsesZeroValue() {
        String line = "|E111|RJ10000001|Ajuste sem valor|0,00|";
        RecordE111 r = parser.parse(line);

        assertEquals(BigDecimal.ZERO, r.value());
    }

    @Test
    void rejectsWrongRegCode() {
        String line = "|E110|2760,00|0,00|2760,00|0,00|1800,00|100,00|1900,00|0,00|0,00|860,00|0,00|860,00|0,00|0,00|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void rejectsMissingFields() {
        String line = "|E111|SP10000001|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void rejectsMalformedLine() {
        String line = "E111|SP10000001|Desc|100,00|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }
}
