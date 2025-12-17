package br.com.efdparser.parser;

import br.com.efdparser.model.RecordC170;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RecordC170ParserTest {

    private final RecordC170Parser parser = new RecordC170Parser();

    private static final String VALID_LINE =
        "|C170|1|PROD001|Produto A - Descricao|10,000|UN|1000,00|0,00|0|000|5101||10000,00|12,00|1200,00|0,00|0,00|0,00|0|50|001|0,00|0,00|0,00|01|10000,00|0,0165||165,00|01|10000,00|0,0760||760,00||";

    @Test
    void parsesAllFields() {
        RecordC170 r = parser.parse(VALID_LINE);

        assertEquals(1, r.itemNumber());
        assertEquals("PROD001", r.itemCode());
        assertEquals("Produto A - Descricao", r.description());
        assertEquals(new BigDecimal("10.000"), r.quantity());
        assertEquals("UN", r.unit());
        assertEquals(new BigDecimal("1000.00"), r.unitValue());
        assertEquals(BigDecimal.ZERO, r.discountValue());
        assertEquals("000", r.icmsCst());
        assertEquals("5101", r.cfop());
        assertEquals(new BigDecimal("10000.00"), r.icmsBase());
        assertEquals(new BigDecimal("12.00"), r.icmsRate());
        assertEquals(new BigDecimal("1200.00"), r.icmsValue());
        assertEquals(BigDecimal.ZERO, r.icmsStBase());
        assertEquals(BigDecimal.ZERO, r.icmsStValue());
    }

    @Test
    void parsesItemWithIcmsSt() {
        String line = "|C170|2|PROD002|Produto B|5,000|CX|200,00|0,00|0|010|5401||1000,00|12,00|120,00|1100,00|2,00|22,00|0|50|001|0,00|0,00|0,00|01|1000,00|0,0165||16,50|01|1000,00|0,0760||76,00||";
        RecordC170 r = parser.parse(line);

        assertEquals(2, r.itemNumber());
        assertEquals("5401", r.cfop());
        assertEquals(new BigDecimal("120.00"), r.icmsValue());
        assertEquals(new BigDecimal("1100.00"), r.icmsStBase());
        assertEquals(new BigDecimal("2.00"), r.icmsStRate());
        assertEquals(new BigDecimal("22.00"), r.icmsStValue());
    }

    @Test
    void treatsEmptyDecimalFieldsAsZero() {
        String line = "|C170|1|PROD003|Item|1,000|UN|500,00||0|000|5101||||||||0|50|001||||01|||||01|||||";
        RecordC170 r = parser.parse(line);

        assertEquals(BigDecimal.ZERO, r.discountValue());
        assertEquals(BigDecimal.ZERO, r.icmsBase());
        assertEquals(BigDecimal.ZERO, r.icmsValue());
    }

    @Test
    void rejectsWrongRegCode() {
        String line = "|E111|SP10000001|Ajuste|100,00|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void rejectsTooFewFields() {
        String line = "|C170|1|PROD|Desc|10,000|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void rejectsInvalidDecimal() {
        String line = "|C170|1|PROD|Desc|INVALID|UN|1000,00|0,00|0|000|5101||1000,00|12,00|120,00|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }
}
