package br.com.efdparser.model;

import java.math.BigDecimal;

/**
 * Registro E110 — Apuração do ICMS — Operações Próprias.
 * Contém os totais consolidados da apuração do ICMS para o período.
 */
public record RecordE110(
    BigDecimal totalDebits,              // VL_TOT_DEBITOS: total de débitos por saídas
    BigDecimal debitAdjustments,         // VL_AJ_DEBITOS: ajustes a débito por doc. fiscal
    BigDecimal totalWithDebitAdjustments,// VL_TOT_AJ_DEBITOS: total outros débitos
    BigDecimal creditReversals,          // VL_ESTORNOS_CRED: estornos de créditos
    BigDecimal totalCredits,             // VL_TOT_CREDITOS: total créditos por entradas
    BigDecimal creditAdjustments,        // VL_AJ_CREDITOS: ajustes a crédito por doc. fiscal
    BigDecimal totalWithCreditAdjustments,// VL_TOT_AJ_CREDITOS: total outros créditos
    BigDecimal debitReversals,           // VL_ESTORNOS_DEB: estornos de débitos
    BigDecimal previousCreditBalance,    // VL_SLD_CREDOR_ANT: saldo credor período anterior
    BigDecimal calculatedBalance,        // VL_SLD_APURADO: saldo devedor antes das deduções
    BigDecimal totalDeductions,          // VL_TOT_DED: total das deduções
    BigDecimal icmsToCollect,            // VL_ICMS_RECOLHER: ICMS a recolher
    BigDecimal creditToCarryForward,     // VL_SLD_CREDOR_TRANSPORTAR: saldo credor a transportar
    BigDecimal specialDebits             // DEB_ESP: débitos especiais / substituição tributária
) {}
