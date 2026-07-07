package com.dgft.irm.dto.request;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DgftIrmDto {

    private String bankRefNumber;
    private String irmIssueDate;
    private String irmNumber;
    private String irmStatus;
    private String ifscCode;
    private String remittanceAdCode;
    private String remittanceDate;
    private String remittanceFCC;
    private BigDecimal remittanceFCAmount;
    private BigDecimal inrCreditAmount;
    private String iecCode;
    private String panNumber;
    private String remitterName;
    private String remitterCountry;
    private String purposeOfRemittance;
    private String bankAccountNo;
}