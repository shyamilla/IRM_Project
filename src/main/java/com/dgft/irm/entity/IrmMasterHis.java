package com.dgft.irm.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dgft_irm_master_his")
@Data
public class IrmMasterHis {

    @Id
    @Column(name = "ID", length = 200)
    private String id;

    @Column(name = "TRIGGER_DATE")
    private LocalDateTime triggerDate;

    @Column(name = "IRM_ID", length = 200)
    private String irmId;

    @Column(name = "IRM_NUMBER", length = 50)
    private String irmNumber;

    @Column(name = "REMITTANCE_AMOUNT", precision = 16, scale = 4)
    private BigDecimal remittanceAmount;

    @Column(name = "REMITTANCE_DATE")
    private LocalDate remittanceDate;

    @Column(name = "AD_CODE_ID", length = 200)
    private String adCodeId;

    @Column(name = "AD_CODE", length = 7)
    private String adCode;

    @Column(name = "REMITTANCE_CURRENCY_ID", length = 200)
    private String remittanceCurrencyId;

    @Column(name = "CURRENCY", length = 3)
    private String currency;

    @Column(name = "ADDED_BY", length = 50)
    private String addedBy;

    @Column(name = "ADDED_DATE")
    private LocalDateTime addedDate;

    @Column(name = "MODIFIED_BY", length = 50)
    private String modifiedBy;

    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @Column(name = "IE_CODE", length = 10)
    private String ieCode;

    @Column(name = "IE_NAME", length = 100)
    private String ieName;

    @Column(name = "REMITTER_NAME", length = 250)
    private String remitterName;

    @Column(name = "REMITTER_COUNTRY_ID", length = 200)
    private String remitterCountryId;

    @Column(name = "REMITTER_COUNTRY", length = 2)
    private String remitterCountry;

    @Column(name = "SWIFT_OTHER_BANK_REF_NUMBER", length = 50)
    private String swiftOtherBankRefNumber;

    @Column(name = "PURPOSE_OF_REMITTANCE", length = 8)
    private String purposeOfRemittance;

    @Column(name = "IFSC_CODE", length = 11)
    private String ifscCode;

    @Column(name = "INR_CREDIT_AMOUNT", precision = 16, scale = 2)
    private BigDecimal inrCreditAmount;

    @Column(name = "EXCHANGE_RATE", precision = 5, scale = 2)
    private BigDecimal exchangeRate;

    @Column(name = "PAN_NUMBER", length = 10)
    private String panNumber;

    @Column(name = "MODE_OF_PAYMENT", length = 50)
    private String modeOfPayment;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "REMARKS", length = 200)
    private String remarks;

    @Column(name = "INBOUND_FILE_NAME", length = 100)
    private String inboundFileName;

    @Column(name = "FLAG", length = 1)
    private String flag;

    @Column(name = "DGFT_FLAG", length = 1)
    private String dgftFlag;

    @Column(name = "ERROR_CODES", length = 200)
    private String errorCodes;

    @Column(name = "CHECKER_REMARKS", length = 250)
    private String checkerRemarks;

    @Column(name = "BANK_UNIQUE_TRANSACTION_ID", length = 25)
    private String bankUniqueTransactionId;

    @Column(name = "BANK_REFERENCE_NUMBER", length = 20)
    private String bankReferenceNumber;

    @Column(name = "IRM_ISSUE_DATE")
    private LocalDate irmIssueDate;

    @Column(name = "REMITTANCE_TYPE", length = 1)
    private String remittanceType;

    @Column(name = "BANK_ACCOUNT_NUMBER", length = 25)
    private String bankAccountNumber;

    @Column(name = "DGFT_STATUS", length = 250)
    private String dgftStatus;

    @Column(name = "PROCESS_STATUS", length = 20)
    private String processStatus;

    @Column(name = "MASTER_DETAIL_STATUS", length = 50)
    private String masterDetailStatus;

    @Column(name = "BILL_REF_NUMBER", length = 100)
    private String billRefNumber;

    @Column(name = "TRIGGER_STATUS", length = 20)
    private String triggerStatus;
}
