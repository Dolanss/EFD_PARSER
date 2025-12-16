package br.com.efdparser.parser;

import br.com.efdparser.model.Record0000;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Record0000ParserTest {

    private final Record0000Parser parser = new Record0000Parser();

    @Test
    void parsesAllFields() {
        String line = "|0000|011|0|01012024|31012024|EMPRESA COMERCIAL LTDA|12345678000190||SP|123456789|3550308|98765||A|1|";
        Record0000 r = parser.parse(line);

        assertEquals("011", r.version());
        assertEquals("0", r.finality());
        assertEquals("01012024", r.startDate());
        assertEquals("31012024", r.endDate());
        assertEquals("EMPRESA COMERCIAL LTDA", r.company());
        assertEquals("12345678000190", r.cnpj());
        assertEquals("", r.cpf());
        assertEquals("SP", r.uf());
        assertEquals("123456789", r.stateRegistration());
        assertEquals("3550308", r.municipalityCode());
        assertEquals("98765", r.municipalRegistration());
        assertEquals("", r.suframa());
        assertEquals("A", r.profile());
        assertEquals("1", r.activity());
    }

    @Test
    void parsesWithOptionalFieldsAbsent() {
        // CPF, IM, SUFRAMA are optional — only mandatory fields up to CNPJ
        String line = "|0000|011|0|01012024|31012024|EMPRESA LTDA|12345678000190|";
        Record0000 r = parser.parse(line);

        assertEquals("EMPRESA LTDA", r.company());
        assertEquals("12345678000190", r.cnpj());
        assertEquals("", r.uf());
    }

    @Test
    void rejectsWrongRegCode() {
        String line = "|C100|1|0|PART|55|00|001|000001||01012024|01012024|1000,00|0|||1000,00|0||||1000,00|120,00|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void rejectsMissingLeadingPipe() {
        String line = "0000|011|0|01012024|31012024|EMPRESA|12345678000190||SP|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void rejectsMissingTrailingPipe() {
        String line = "|0000|011|0|01012024|31012024|EMPRESA|12345678000190||SP";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }

    @Test
    void rejectsTooFewFields() {
        String line = "|0000|011|";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(line));
    }
}
