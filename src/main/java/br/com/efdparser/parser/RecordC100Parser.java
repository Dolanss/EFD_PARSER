package br.com.efdparser.parser;

import br.com.efdparser.model.RecordC100;

public class RecordC100Parser extends BaseParser {

    // Field indices (fields[0] = REG)
    private static final int IND_OPER       = 1;
    private static final int IND_EMIT       = 2;
    private static final int COD_PART       = 3;
    private static final int COD_MOD        = 4;
    private static final int COD_SIT        = 5;
    private static final int SER            = 6;
    private static final int NUM_DOC        = 7;
    private static final int CHV_NFE        = 8;
    private static final int DT_DOC         = 9;
    private static final int DT_E_S         = 10;
    private static final int VL_DOC         = 11;
    // 12 = IND_PGTO
    private static final int VL_DESC        = 13;
    // 14 = VL_ABAT_NT
    private static final int VL_MERC        = 15;
    // 16 = IND_FRT, 17 = VL_FRT, 18 = VL_SEG, 19 = VL_OUT_DA
    private static final int VL_BC_ICMS     = 20;
    private static final int VL_ICMS        = 21;
    private static final int VL_BC_ICMS_ST  = 22;
    private static final int VL_ICMS_ST     = 23;
    private static final int VL_IPI         = 24;
    private static final int VL_PIS         = 25;
    private static final int VL_COFINS      = 26;

    private static final int MIN_FIELDS = 22;

    public RecordC100 parse(String line) {
        return parse(splitFields(line));
    }

    public RecordC100 parse(String[] fields) {
        requireReg(fields, "C100");
        requireMinFields(fields, MIN_FIELDS, "C100");
        return new RecordC100(
            str(fields, IND_OPER),
            str(fields, IND_EMIT),
            str(fields, COD_PART),
            str(fields, COD_MOD),
            str(fields, COD_SIT),
            str(fields, SER),
            str(fields, NUM_DOC),
            str(fields, CHV_NFE),
            str(fields, DT_DOC),
            str(fields, DT_E_S),
            dec(fields, VL_DOC),
            dec(fields, VL_DESC),
            dec(fields, VL_MERC),
            dec(fields, VL_BC_ICMS),
            dec(fields, VL_ICMS),
            dec(fields, VL_BC_ICMS_ST),
            dec(fields, VL_ICMS_ST),
            dec(fields, VL_IPI),
            dec(fields, VL_PIS),
            dec(fields, VL_COFINS)
        );
    }
}
