package br.com.efdparser.parser;

import br.com.efdparser.model.RecordE110;

public class RecordE110Parser extends BaseParser {

    // Field indices (fields[0] = REG)
    private static final int VL_TOT_DEBITOS           = 1;
    private static final int VL_AJ_DEBITOS            = 2;
    private static final int VL_TOT_AJ_DEBITOS        = 3;
    private static final int VL_ESTORNOS_CRED         = 4;
    private static final int VL_TOT_CREDITOS          = 5;
    private static final int VL_AJ_CREDITOS           = 6;
    private static final int VL_TOT_AJ_CREDITOS       = 7;
    private static final int VL_ESTORNOS_DEB          = 8;
    private static final int VL_SLD_CREDOR_ANT        = 9;
    private static final int VL_SLD_APURADO           = 10;
    private static final int VL_TOT_DED               = 11;
    private static final int VL_ICMS_RECOLHER         = 12;
    private static final int VL_SLD_CREDOR_TRANSPORTAR = 13;
    private static final int DEB_ESP                  = 14;

    private static final int MIN_FIELDS = 13;

    public RecordE110 parse(String line) {
        return parse(splitFields(line));
    }

    public RecordE110 parse(String[] fields) {
        requireReg(fields, "E110");
        requireMinFields(fields, MIN_FIELDS, "E110");
        return new RecordE110(
            dec(fields, VL_TOT_DEBITOS),
            dec(fields, VL_AJ_DEBITOS),
            dec(fields, VL_TOT_AJ_DEBITOS),
            dec(fields, VL_ESTORNOS_CRED),
            dec(fields, VL_TOT_CREDITOS),
            dec(fields, VL_AJ_CREDITOS),
            dec(fields, VL_TOT_AJ_CREDITOS),
            dec(fields, VL_ESTORNOS_DEB),
            dec(fields, VL_SLD_CREDOR_ANT),
            dec(fields, VL_SLD_APURADO),
            dec(fields, VL_TOT_DED),
            dec(fields, VL_ICMS_RECOLHER),
            dec(fields, VL_SLD_CREDOR_TRANSPORTAR),
            dec(fields, DEB_ESP)
        );
    }
}
