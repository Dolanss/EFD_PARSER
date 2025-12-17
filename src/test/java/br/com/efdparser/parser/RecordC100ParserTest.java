package br.com.efdparser.parser;

import br.com.efdparser.model.RecordC100;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RecordC100ParserTest {

    private final RecordC100Parser parser = new RecordC100Parser();

    private static final String SAIDA_LINE =
        "|C100|1|0|CLIENTE001|55|00|001|000001|35240112345678000190550010000001234567890000" +
        "|01012024|01012024|12000,00|0|500,00||11500,00|0|0,00|0,00|0,00|10000,00|1200,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|";

    private static final String ENTRADA_LINE =
        "|C100|0|1|FORNEC001|55|00|001|000101|35240187654321000100550010000001018765432100" +
        "|03012024|03012024|10000,00|0|0,00||10000,00|0|0,00|0,00|0,00|10000,00|1200,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|";

    @Test
    void parsesSaidaDocument() {
        RecordC100 r = parser.parse(SAIDA_LINE);

        assertEquals("1", r.operationType());
        assertEquals("0", r.emitterType());
        assertEquals("CLIENTE001", r.participantCode());
        assertEquals("55", r.documentModel());
        assertEquals("001", r.series());
        assertEquals("000001", r.documentNumber());
        assertEquals("01012024", r.documentDate());
        assertEquals(new BigDecimal("12000.00"), r.totalValue());
        assertEquals(new BigDecimal("500.00"), r.discountValue());
        assertEquals(new BigDecimal("10000.00"), r.icmsBase());
        assertEquals(new BigDecimal("1200.00"), r.icmsValue());
        assertEquals(BigDecimal.ZERO, r.icmsStValue());
    }

    @Test
    void parsesEntradaDocument() {
        RecordC100 r = parser.parse(ENTRADA_LINE);

        assertEquals("0", r.operationType());
        assertEquals("1", r.emitterType());
        assertEquals("FORNEC001", r.participantCode());
        assertEquals(new BigDecimal("10000.00"), r.totalValue());
        assertEquals(new BigDecimal("1200.00"), r.icmsValue());
    }

    @Test
    void parsesThousandsSeparatorInValues() {
        // "1.200,50" should parse as 1200.50
        String line = "|C100|1|0|PART|55|00|001|000001||01012024|01012024|1.200,50|0|0,00||1.200,50|0|0,00|0,00|0,00|1.200,50|144,06|0,00|0,00|0,00|0,00|0,00|0,00|0,00|";
        RecordC100 r = parser.parse(line);
        assertEquals(new BigDecimal("1200.50"), r.totalValue());
        assertEquals(new BigDecimal("1200.50"), r.icmsBase());
        assertEquals(new BigDecimal("144.06"), r.icmsValue());
    }

    @Test
    void treatsEmptyDecimalFieldsAsZero() {
        String line = "|C100|1|0|PART|55|00|001|000001||01012024|01012024|5000,00|0|||5000,00|0||||5000,00|600,00|||||||||";
        RecordC100 r = parser.parse(line);
        assertEquals(new BigDecimal("5000.00"), r.totalValue());
        assertEquals(BigDecimal.ZERO, r.discountValue());
        assertEquals(BigDecimal.ZERO, r.icmsStValue());
    }

    @Test
    void rejectsWrongRegCode() {
        String line = "|C170|1|PROD|Desc|10,000|UN|100,00|0,00|0|000|5101||1000,00|12,00|120,00|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void rejectsTooFewFields() {
        String line = "|C100|1|0|PART|55|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }
}
