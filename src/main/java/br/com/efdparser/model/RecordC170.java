package br.com.efdparser.model;

import java.math.BigDecimal;

/**
 * Registro C170 — Itens do Documento (Código 01, 1B, 04 e 55).
 * Representa cada item (produto/serviço) de um documento fiscal C100.
 */
public record RecordC170(
    int itemNumber,          // NUM_ITEM
    String itemCode,         // COD_ITEM
    String description,      // DESCR_COMPL
    BigDecimal quantity,     // QTD
    String unit,             // UN
    BigDecimal unitValue,    // VL_UNIT
    BigDecimal discountValue,// VL_DESC
    String icmsCst,          // CST_ICMS
    String cfop,             // CFOP
    BigDecimal icmsBase,     // VL_BC_ICMS
    BigDecimal icmsRate,     // ALIQ_ICMS
    BigDecimal icmsValue,    // VL_ICMS
    BigDecimal icmsStBase,   // VL_BC_ICMS_ST
    BigDecimal icmsStRate,   // ALIQ_ST
    BigDecimal icmsStValue,  // VL_ICMS_ST
    BigDecimal ipiValue      // VL_IPI
) {}
