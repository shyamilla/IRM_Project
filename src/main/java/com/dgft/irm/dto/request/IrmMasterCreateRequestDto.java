package com.dgft.irm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Used by IrmMasterController for direct/manual creation of an IRM record
 * (as opposed to the bulk CSV ingestion path). Goes through the same
 * IrmValidationService rules before being persisted.
 */
@Data
public class IrmMasterCreateRequestDto {
    @NotBlank
    private String irmNumber;
    private BigDecimal remittanceAmount;
    private LocalDate remittanceDate;
    private String adCode;
    private String currency;
    private String ieCode;
    private String remitterName;
    private String remitterCountry;
    private String purposeOfRemittance;
    private String ifscCode;
    private BigDecimal inrCreditAmount;
    private String panNumber;
    private String bankReferenceNumber;
    private String bankAccountNumber;
    private LocalDate irmIssueDate;
}
