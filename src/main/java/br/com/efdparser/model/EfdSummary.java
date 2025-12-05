package br.com.efdparser.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"period", "company", "cnpj", "uf", "startDate", "endDate",
                    "apuration", "adjustments", "documentCount", "itemCount", "documents", "warnings"})
public class EfdSummary {

    public String period;
    public String company;
    public String cnpj;
    public String uf;
    public String startDate;
    public String endDate;
    public IcmsApuration apuration;
    public List<AdjustmentEntry> adjustments;
    public int documentCount;
    public int itemCount;
    public List<DocumentEntry> documents;
    public int warnings;

    @JsonPropertyOrder({"totalDebits", "debitAdjustments", "totalWithDebitAdjustments",
                        "creditReversals", "totalCredits", "creditAdjustments", "totalWithCreditAdjustments",
                        "debitReversals", "previousCreditBalance", "calculatedBalance",
                        "totalDeductions", "icmsToCollect", "creditToCarryForward", "specialDebits"})
    public static class IcmsApuration {
        public BigDecimal totalDebits = BigDecimal.ZERO;
        public BigDecimal debitAdjustments = BigDecimal.ZERO;
        public BigDecimal totalWithDebitAdjustments = BigDecimal.ZERO;
        public BigDecimal creditReversals = BigDecimal.ZERO;
        public BigDecimal totalCredits = BigDecimal.ZERO;
        public BigDecimal creditAdjustments = BigDecimal.ZERO;
        public BigDecimal totalWithCreditAdjustments = BigDecimal.ZERO;
        public BigDecimal debitReversals = BigDecimal.ZERO;
        public BigDecimal previousCreditBalance = BigDecimal.ZERO;
        public BigDecimal calculatedBalance = BigDecimal.ZERO;
        public BigDecimal totalDeductions = BigDecimal.ZERO;
        public BigDecimal icmsToCollect = BigDecimal.ZERO;
        public BigDecimal creditToCarryForward = BigDecimal.ZERO;
        public BigDecimal specialDebits = BigDecimal.ZERO;
    }

    public static class AdjustmentEntry {
        public String code;
        public String description;
        public BigDecimal value;
    }

    @JsonPropertyOrder({"documentNumber", "series", "operationType", "documentDate",
                        "nfeKey", "totalValue", "icmsBase", "icmsValue", "items"})
    public static class DocumentEntry {
        public String documentNumber;
        public String series;
        public String operationType;
        public String documentDate;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public String nfeKey;
        public BigDecimal totalValue;
        public BigDecimal icmsBase;
        public BigDecimal icmsValue;
        public List<ItemEntry> items;
    }

    @JsonPropertyOrder({"itemNumber", "itemCode", "description", "quantity", "unit",
                        "unitValue", "cfop", "icmsCst", "icmsBase", "icmsRate", "icmsValue"})
    public static class ItemEntry {
        public int itemNumber;
        public String itemCode;
        public String description;
        public BigDecimal quantity;
        public String unit;
        public BigDecimal unitValue;
        public String cfop;
        public String icmsCst;
        public BigDecimal icmsBase;
        public BigDecimal icmsRate;
        public BigDecimal icmsValue;
    }
}
