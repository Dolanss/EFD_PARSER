package br.com.efdparser;

import br.com.efdparser.model.*;
import br.com.efdparser.parser.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

/**
 * Reads an EFD Fiscal flat-text file line by line and produces an {@link EfdSummary}.
 *
 * <p>Supported records: 0000, C100, C170, E110, E111.
 * Unknown records are skipped with a log message at FINE level.
 * Malformed lines and parse errors log a WARNING and are skipped.
 */
public class EfdParser {

    private static final Logger log = Logger.getLogger(EfdParser.class.getName());

    // EFD files in Brazil use ISO-8859-1 (Latin-1) encoding
    private static final Charset EFD_CHARSET = Charset.forName("ISO-8859-1");

    private final Record0000Parser parser0000 = new Record0000Parser();
    private final RecordC100Parser parserC100 = new RecordC100Parser();
    private final RecordC170Parser parserC170 = new RecordC170Parser();
    private final RecordE110Parser parserE110 = new RecordE110Parser();
    private final RecordE111Parser parserE111 = new RecordE111Parser();

    /**
     * Parses the EFD file at the given path.
     *
     * @param inputFile    path to the EFD Fiscal text file
     * @param periodFilter optional "YYYY-MM" filter; if set, a warning is logged when the
     *                     file header period does not match — parsing continues regardless
     */
    public EfdSummary parse(Path inputFile, String periodFilter) throws IOException {
        Record0000 header = null;
        RecordE110 apuration = null;
        List<RecordE111> adjustments = new ArrayList<>();

        // Each C100 document and its child C170 items
        List<RecordC100> documents = new ArrayList<>();
        List<List<RecordC170>> documentItems = new ArrayList<>();

        int lineNumber = 0;
        int warnings = 0;

        try (var reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFile.toFile()), EFD_CHARSET))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                if (!line.startsWith("|")) {
                    log.warning("Line " + lineNumber + ": malformed (no leading '|'), skipping");
                    warnings++;
                    continue;
                }
                if (!line.endsWith("|")) {
                    log.warning("Line " + lineNumber + ": malformed (no trailing '|'), skipping");
                    warnings++;
                    continue;
                }

                String[] fields = line.substring(1, line.length() - 1).split("\\|", -1);
                String reg = fields[0].trim();

                try {
                    switch (reg) {
                        case "0000" -> header = parser0000.parse(fields);
                        case "C100" -> {
                            documents.add(parserC100.parse(fields));
                            documentItems.add(new ArrayList<>());
                        }
                        case "C170" -> {
                            if (!documentItems.isEmpty()) {
                                documentItems.get(documentItems.size() - 1)
                                             .add(parserC170.parse(fields));
                            } else {
                                log.warning("Line " + lineNumber + ": C170 without preceding C100, skipping");
                                warnings++;
                            }
                        }
                        case "E110" -> {
                            if (apuration != null) {
                                log.warning("Line " + lineNumber + ": duplicate E110, overwriting previous");
                            }
                            apuration = parserE110.parse(fields);
                        }
                        case "E111" -> adjustments.add(parserE111.parse(fields));
                        // Control / block records — counted for integrity but not parsed
                        case "0001", "0005", "0990",
                             "C001", "C190", "C990",
                             "E001", "E100", "E990",
                             "9001", "9900", "9990", "9999" -> { /* intentionally skipped */ }
                        default -> log.fine("Line " + lineNumber + ": unknown record '" + reg + "', skipping");
                    }
                } catch (IllegalArgumentException e) {
                    log.warning("Line " + lineNumber + " [" + reg + "]: " + e.getMessage());
                    warnings++;
                }
            }
        }

        if (header == null) {
            throw new IOException("EFD file did not contain a valid 0000 (header) record");
        }

        String filePeriod = toPeriod(header.startDate());
        if (periodFilter != null && !periodFilter.equals(filePeriod)) {
            log.warning("Period filter '" + periodFilter + "' does not match file period '" + filePeriod
                        + "' — results will reflect the actual file period");
        }

        return buildSummary(header, apuration, adjustments, documents, documentItems, warnings);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private EfdSummary buildSummary(Record0000 header,
                                    RecordE110 apuration,
                                    List<RecordE111> adjustments,
                                    List<RecordC100> documents,
                                    List<List<RecordC170>> documentItems,
                                    int warnings) {

        var summary = new EfdSummary();
        summary.period   = toPeriod(header.startDate());
        summary.company  = header.company();
        summary.cnpj     = header.cnpj();
        summary.uf       = header.uf();
        summary.startDate = formatDate(header.startDate());
        summary.endDate   = formatDate(header.endDate());
        summary.warnings  = warnings;

        // E110 apuration — authoritative ICMS totals for the period
        summary.apuration = buildApuration(apuration);

        // E111 adjustments
        summary.adjustments = adjustments.stream().map(e -> {
            var a = new EfdSummary.AdjustmentEntry();
            a.code        = e.adjustmentCode();
            a.description = e.description();
            a.value       = scale2(e.value());
            return a;
        }).toList();

        // C100/C170 documents
        var docEntries = new ArrayList<EfdSummary.DocumentEntry>();
        int totalItems = 0;

        for (int i = 0; i < documents.size(); i++) {
            var doc = documents.get(i);
            var items = documentItems.get(i);

            var entry = new EfdSummary.DocumentEntry();
            entry.documentNumber = doc.documentNumber();
            entry.series         = doc.series();
            entry.operationType  = "0".equals(doc.operationType()) ? "ENTRADA" : "SAIDA";
            entry.documentDate   = formatDate(doc.documentDate());
            entry.nfeKey         = doc.nfeKey();
            entry.totalValue     = scale2(doc.totalValue());
            entry.icmsBase       = scale2(doc.icmsBase());
            entry.icmsValue      = scale2(doc.icmsValue());

            entry.items = items.stream().map(item -> {
                var ie = new EfdSummary.ItemEntry();
                ie.itemNumber  = item.itemNumber();
                ie.itemCode    = item.itemCode();
                ie.description = item.description();
                ie.quantity    = item.quantity();
                ie.unit        = item.unit();
                ie.unitValue   = scale2(item.unitValue());
                ie.cfop        = item.cfop();
                ie.icmsCst     = item.icmsCst();
                ie.icmsBase    = scale2(item.icmsBase());
                ie.icmsRate    = item.icmsRate();
                ie.icmsValue   = scale2(item.icmsValue());
                return ie;
            }).toList();

            totalItems += items.size();
            docEntries.add(entry);
        }

        summary.documentCount = documents.size();
        summary.itemCount     = totalItems;
        summary.documents     = docEntries;

        return summary;
    }

    private EfdSummary.IcmsApuration buildApuration(RecordE110 e110) {
        var ap = new EfdSummary.IcmsApuration();
        if (e110 == null) return ap; // no E110 found — return zeroed apuration

        ap.totalDebits                 = scale2(e110.totalDebits());
        ap.debitAdjustments            = scale2(e110.debitAdjustments());
        ap.totalWithDebitAdjustments   = scale2(e110.totalWithDebitAdjustments());
        ap.creditReversals             = scale2(e110.creditReversals());
        ap.totalCredits                = scale2(e110.totalCredits());
        ap.creditAdjustments           = scale2(e110.creditAdjustments());
        ap.totalWithCreditAdjustments  = scale2(e110.totalWithCreditAdjustments());
        ap.debitReversals              = scale2(e110.debitReversals());
        ap.previousCreditBalance       = scale2(e110.previousCreditBalance());
        ap.calculatedBalance           = scale2(e110.calculatedBalance());
        ap.totalDeductions             = scale2(e110.totalDeductions());
        ap.icmsToCollect               = scale2(e110.icmsToCollect());
        ap.creditToCarryForward        = scale2(e110.creditToCarryForward());
        ap.specialDebits               = scale2(e110.specialDebits());
        return ap;
    }

    /** Converts EFD date "DDMMAAAA" → "DD/MM/AAAA" for display. */
    private static String formatDate(String ddmmaaaa) {
        if (ddmmaaaa == null || ddmmaaaa.length() < 8) return ddmmaaaa;
        return ddmmaaaa.substring(0, 2) + "/" + ddmmaaaa.substring(2, 4) + "/" + ddmmaaaa.substring(4, 8);
    }

    /** Converts EFD date "DDMMAAAA" → "AAAA-MM" competency period. */
    static String toPeriod(String ddmmaaaa) {
        if (ddmmaaaa == null || ddmmaaaa.length() < 8) return "";
        return ddmmaaaa.substring(4, 8) + "-" + ddmmaaaa.substring(2, 4);
    }

    private static BigDecimal scale2(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v.setScale(2, RoundingMode.HALF_UP);
    }
}
