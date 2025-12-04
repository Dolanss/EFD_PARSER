package br.com.efdparser.model;

import java.math.BigDecimal;

/**
 * Registro C100 — Nota Fiscal (Código 01, 1B, 04 e 55).
 * Representa cabeçalho do documento fiscal com valores consolidados de ICMS.
 */
public record RecordC100(
    String operationType,   // IND_OPER: 0=entrada, 1=saída
    String emitterType,     // IND_EMIT: 0=emissão própria, 1=terceiros
    String participantCode, // COD_PART
    String documentModel,   // COD_MOD: 55=NF-e, 65=NFC-e
    String situation,       // COD_SIT: 00=normal, 06=cancelada, etc.
    String series,          // SER
    String documentNumber,  // NUM_DOC
    String nfeKey,          // CHV_NFE
    String documentDate,    // DT_DOC: DDMMAAAA
    String entrySaleDate,   // DT_E_S: DDMMAAAA
    BigDecimal totalValue,      // VL_DOC
    BigDecimal discountValue,   // VL_DESC
    BigDecimal merchandiseValue,// VL_MERC
    BigDecimal icmsBase,        // VL_BC_ICMS
    BigDecimal icmsValue,       // VL_ICMS
    BigDecimal icmsStBase,      // VL_BC_ICMS_ST
    BigDecimal icmsStValue,     // VL_ICMS_ST
    BigDecimal ipiValue,        // VL_IPI
    BigDecimal pisValue,        // VL_PIS
    BigDecimal cofinsValue      // VL_COFINS
) {}
