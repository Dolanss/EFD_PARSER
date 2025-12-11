package br.com.efdparser.parser;

import br.com.efdparser.model.RecordC170;

public class RecordC170Parser extends BaseParser {

    // Field indices (fields[0] = REG)
    private static final int NUM_ITEM       = 1;
    private static final int COD_ITEM       = 2;
    private static final int DESCR_COMPL    = 3;
    private static final int QTD            = 4;
    private static final int UN             = 5;
    private static final int VL_UNIT        = 6;
    private static final int VL_DESC        = 7;
    // 8 = IND_MOV
    private static final int CST_ICMS       = 9;
    private static final int CFOP           = 10;
    // 11 = COD_NAT
    private static final int VL_BC_ICMS     = 12;
    private static final int ALIQ_ICMS      = 13;
    private static final int VL_ICMS        = 14;
    private static final int VL_BC_ICMS_ST  = 15;
    private static final int ALIQ_ST        = 16;
    private static final int VL_ICMS_ST     = 17;
    // 18 = IND_APUR, 19 = CST_IPI, 20 = COD_ENQ, 21 = VL_BC_IPI, 22 = ALIQ_IPI
    private static final int VL_IPI         = 23;

    private static final int MIN_FIELDS = 15;

    public RecordC170 parse(String line) {
        return parse(splitFields(line));
    }

    public RecordC170 parse(String[] fields) {
        requireReg(fields, "C170");
        requireMinFields(fields, MIN_FIELDS, "C170");
        return new RecordC170(
            integer(fields, NUM_ITEM),
            str(fields, COD_ITEM),
            str(fields, DESCR_COMPL),
            dec(fields, QTD),
            str(fields, UN),
            dec(fields, VL_UNIT),
            dec(fields, VL_DESC),
            str(fields, CST_ICMS),
            str(fields, CFOP),
            dec(fields, VL_BC_ICMS),
            dec(fields, ALIQ_ICMS),
            dec(fields, VL_ICMS),
            dec(fields, VL_BC_ICMS_ST),
            dec(fields, ALIQ_ST),
            dec(fields, VL_ICMS_ST),
            dec(fields, VL_IPI)
        );
    }
}
