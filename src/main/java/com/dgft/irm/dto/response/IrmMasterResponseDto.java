package com.dgft.irm.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


//  Response DTO containing IRM Master details returned to the client.
//  Represents the processed IRM record retrieved from the database.
 
@Data
public class IrmMasterResponseDto {
    private String id;
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
    private String status;
    private String flag;
    private String dgftFlag;
    private String dgftStatus;
    private String inboundFileName;
    private LocalDateTime addedDate;
}
