package br.com.efdparser.model;

/**
 * Registro 0000 — Abertura do Arquivo Digital e Identificação da Entidade.
 * Posições de campo conforme Guia Prático EFD ICMS/IPI.
 */
public record Record0000(
    String version,           // COD_VER: versão do leiaute
    String finality,          // COD_FIN: 0=original, 1=substituto
    String startDate,         // DT_INI: DDMMAAAA
    String endDate,           // DT_FIN: DDMMAAAA
    String company,           // NOME: razão social
    String cnpj,              // CNPJ
    String cpf,               // CPF (exclusivo com CNPJ)
    String uf,                // UF
    String stateRegistration, // IE
    String municipalityCode,  // COD_MUN
    String municipalRegistration, // IM
    String suframa,           // SUFRAMA
    String profile,           // IND_PERFIL: A, B ou C
    String activity           // IND_ATIV: 0=industrial, 1=outros
) {}
