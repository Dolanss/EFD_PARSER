package br.com.efdparser.parser;

import br.com.efdparser.model.RecordE111;

public class RecordE111Parser extends BaseParser {

    // Field indices (fields[0] = REG)
    private static final int COD_AJ_APUR    = 1;
    private static final int DESCR_COMPL_AJ = 2;
    private static final int VL_AJ_APUR     = 3;

    private static final int MIN_FIELDS = 4;

    public RecordE111 parse(String line) {
        return parse(splitFields(line));
    }

    public RecordE111 parse(String[] fields) {
        requireReg(fields, "E111");
        requireMinFields(fields, MIN_FIELDS, "E111");
        return new RecordE111(
            str(fields, COD_AJ_APUR),
            str(fields, DESCR_COMPL_AJ),
            dec(fields, VL_AJ_APUR)
        );
    }
}
