package br.com.efdparser;

import br.com.efdparser.model.EfdSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class EfdParserIntegrationTest {

    private final EfdParser parser = new EfdParser();

    /** Minimal single-document EFD for targeted assertions. */
    private static final String MINIMAL_EFD = """
        |0000|011|0|01012024|31012024|EMPRESA TESTE LTDA|98765432000111||RJ|RJ-123456|3304557|654321||A|1|
        |0001|0|
        |C001|0|
        |C100|1|0|CLIENTERJ|55|00|001|000099||01012024|01012024|5000,00|0|0,00||5000,00|0|0,00|0,00|0,00|5000,00|600,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|
        |C170|1|PROD-RJ|Item de Teste|10,000|UN|500,00|0,00|0|000|5101||5000,00|12,00|600,00|0,00|0,00|0,00|0|50|001|0,00|0,00|0,00|01|5000,00|0,0165||82,50|01|5000,00|0,0760||380,00||
        |C990|3|
        |E001|0|
        |E110|600,00|0,00|600,00|0,00|0,00|0,00|0,00|0,00|0,00|600,00|0,00|600,00|0,00|0,00|
        |E990|2|
        |9999|8|
        """;

    @Test
    void parsesHeaderMetadata(@TempDir Path tmp) throws IOException {
        Path file = writeEfd(tmp, MINIMAL_EFD);
        EfdSummary s = parser.parse(file, null);

        assertEquals("2024-01", s.period);
        assertEquals("EMPRESA TESTE LTDA", s.company);
        assertEquals("98765432000111", s.cnpj);
        assertEquals("RJ", s.uf);
        assertEquals("01/01/2024", s.startDate);
        assertEquals("31/01/2024", s.endDate);
        assertEquals(0, s.warnings);
    }

    @Test
    void parsesDocumentCounts(@TempDir Path tmp) throws IOException {
        Path file = writeEfd(tmp, MINIMAL_EFD);
        EfdSummary s = parser.parse(file, null);

        assertEquals(1, s.documentCount);
        assertEquals(1, s.itemCount);
        assertNotNull(s.documents);
        assertEquals(1, s.documents.size());
    }

    @Test
    void parsesDocumentDetails(@TempDir Path tmp) throws IOException {
        Path file = writeEfd(tmp, MINIMAL_EFD);
        EfdSummary s = parser.parse(file, null);

        var doc = s.documents.get(0);
        assertEquals("000099", doc.documentNumber);
        assertEquals("001", doc.series);
        assertEquals("SAIDA", doc.operationType);
        assertEquals("01/01/2024", doc.documentDate);
        assertEquals(new BigDecimal("5000.00"), doc.totalValue);
        assertEquals(new BigDecimal("5000.00"), doc.icmsBase);
        assertEquals(new BigDecimal("600.00"), doc.icmsValue);

        var item = doc.items.get(0);
        assertEquals(1, item.itemNumber);
        assertEquals("PROD-RJ", item.itemCode);
        assertEquals("UN", item.unit);
        assertEquals(new BigDecimal("12.00"), item.icmsRate);
        assertEquals(new BigDecimal("600.00"), item.icmsValue);
    }

    @Test
    void parsesE110Apuration(@TempDir Path tmp) throws IOException {
        Path file = writeEfd(tmp, MINIMAL_EFD);
        EfdSummary s = parser.parse(file, null);

        assertNotNull(s.apuration);
        assertEquals(new BigDecimal("600.00"), s.apuration.totalDebits);
        assertEquals(new BigDecimal("600.00"), s.apuration.icmsToCollect);
        assertEquals(BigDecimal.ZERO.setScale(2), s.apuration.totalCredits);
        assertEquals(BigDecimal.ZERO.setScale(2), s.apuration.creditToCarryForward);
    }

    @Test
    void parsesSampleFileWithFiveDocuments() throws IOException {
        // Use the sample file bundled in the project
        Path sampleFile = Path.of("sample/efd_fiscal_sample.txt");
        if (!Files.exists(sampleFile)) {
            // Fallback for CI: resolve relative to project root
            sampleFile = Path.of(System.getProperty("user.dir"), "sample/efd_fiscal_sample.txt");
        }
        assumeFileExists(sampleFile);

        EfdSummary s = parser.parse(sampleFile, "2024-01");

        assertEquals("2024-01", s.period);
        assertEquals("12345678000190", s.cnpj);
        assertEquals("SP", s.uf);
        assertEquals(5, s.documentCount);
        assertEquals(5, s.itemCount);

        // E110 from sample: 2760 debits, 1800 credits, 100 credit adjustment → 860 to collect
        assertEquals(new BigDecimal("2760.00"), s.apuration.totalDebits);
        assertEquals(new BigDecimal("1800.00"), s.apuration.totalCredits);
        assertEquals(new BigDecimal("100.00"), s.apuration.creditAdjustments);
        assertEquals(new BigDecimal("860.00"), s.apuration.icmsToCollect);
        assertEquals(BigDecimal.ZERO.setScale(2), s.apuration.creditToCarryForward);

        // E111 adjustment
        assertEquals(1, s.adjustments.size());
        assertEquals("SP10000001", s.adjustments.get(0).code);
        assertEquals(new BigDecimal("100.00"), s.adjustments.get(0).value);
    }

    @Test
    void malformedLinesAreCountedAsWarnings(@TempDir Path tmp) throws IOException {
        String efd = """
            |0000|011|0|01012024|31012024|EMPRESA|12345678000190||SP|IE001|3550308|||A|1|
            THIS LINE HAS NO PIPES
            |C001|0|
            |C990|1|
            |E001|0|
            |E110|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|
            |E990|2|
            |9999|5|
            """;
        Path file = writeEfd(tmp, efd);
        EfdSummary s = parser.parse(file, null);

        assertTrue(s.warnings >= 1, "Expected at least 1 warning for malformed line");
    }

    @Test
    void lineWithoutTrailingPipeIsWarning(@TempDir Path tmp) throws IOException {
        String efd = """
            |0000|011|0|01012024|31012024|EMPRESA|12345678000190||SP|IE|3550308|||A|1|
            |C001|0
            |C990|1|
            |E001|0|
            |E110|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|0,00|
            |E990|2|
            |9999|5|
            """;
        Path file = writeEfd(tmp, efd);
        EfdSummary s = parser.parse(file, null);

        assertTrue(s.warnings >= 1);
    }

    @Test
    void missingHeaderThrowsIOException(@TempDir Path tmp) throws IOException {
        String efd = "|C001|0|\n|C990|1|\n";
        Path file = writeEfd(tmp, efd);

        assertThrows(IOException.class, () -> parser.parse(file, null));
    }

    @Test
    void periodConversion() {
        assertEquals("2024-01", EfdParser.toPeriod("01012024"));
        assertEquals("2023-12", EfdParser.toPeriod("01122023"));
        assertEquals("", EfdParser.toPeriod("short"));
        assertEquals("", EfdParser.toPeriod(null));
    }

    // -------------------------------------------------------------------------
    private Path writeEfd(Path dir, String content) throws IOException {
        var file = dir.resolve("test_efd.txt");
        Files.writeString(file, content, StandardCharsets.ISO_8859_1);
        return file;
    }

    private void assumeFileExists(Path p) {
        org.junit.jupiter.api.Assumptions.assumeTrue(
            Files.exists(p), "Sample file not found at " + p + ", skipping integration test");
    }
}
