package br.com.efdparser.parser;

import br.com.efdparser.model.Record0000;

public class Record0000Parser extends BaseParser {

    // Field indices (fields[0] = REG)
    private static final int COD_VER    = 1;
    private static final int COD_FIN    = 2;
    private static final int DT_INI     = 3;
    private static final int DT_FIN     = 4;
    private static final int NOME       = 5;
    private static final int CNPJ       = 6;
    private static final int CPF        = 7;
    private static final int UF         = 8;
    private static final int IE         = 9;
    private static final int COD_MUN    = 10;
    private static final int IM         = 11;
    private static final int SUFRAMA    = 12;
    private static final int IND_PERFIL = 13;
    private static final int IND_ATIV   = 14;

    private static final int MIN_FIELDS = 6; // at minimum up to NOME

    public Record0000 parse(String line) {
        return parse(splitFields(line));
    }

    public Record0000 parse(String[] fields) {
        requireReg(fields, "0000");
        requireMinFields(fields, MIN_FIELDS, "0000");
        return new Record0000(
            str(fields, COD_VER),
            str(fields, COD_FIN),
            str(fields, DT_INI),
            str(fields, DT_FIN),
            str(fields, NOME),
            str(fields, CNPJ),
            str(fields, CPF),
            str(fields, UF),
            str(fields, IE),
            str(fields, COD_MUN),
            str(fields, IM),
            str(fields, SUFRAMA),
            str(fields, IND_PERFIL),
            str(fields, IND_ATIV)
        );
    }
}
