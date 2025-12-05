package br.com.efdparser.model;

import java.math.BigDecimal;

/**
 * Registro E111 — Ajuste/Benefício/Incentivo da Apuração do ICMS.
 * Cada ocorrência representa um código de ajuste específico (ex: crédito outorgado).
 */
public record RecordE111(
    String adjustmentCode, // COD_AJ_APUR: código tabela 5.1.1 (ex: SP10000001)
    String description,    // DESCR_COMPL_AJ
    BigDecimal value       // VL_AJ_APUR
) {}
