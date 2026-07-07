package com.dgft.irm.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**


 * CSV File
 *      ↓
 * IrmCsvRequestDto
 *      ↓
 * Bean Validation
 *      ↓
 * Business Validation
 *      ↓
 * Entity Mapping
 *      ↓
 * IRM_MASTER Database Table
 */

@Data
public class IrmCsvRequestDto {

    private int rowNumber;

    @NotBlank(message = "IRM Number is mandatory")
    @Size(max = 50, message = "IRM Number must be max 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "IRM Number must be alphanumeric")
    private String irmRefNumber;

    @NotBlank(message = "Remittance Amount is mandatory")
    @Pattern(regexp = "^\\d+(\\.\\d{1,4})?$", message = "Remittance Amount must be numeric")
    private String remittanceAmount;

    @NotBlank(message = "Remittance Date is mandatory")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Remittance Date must be dd/MM/yyyy")
    private String remittanceDate;

    @NotBlank(message = "AD Code is mandatory")
    @Size(max = 7, message = "AD Code must be max 7 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "AD Code must be alphanumeric")
    private String adCode;

    @NotBlank(message = "Currency is mandatory")
    @Size(max = 3, message = "Currency must be max 3 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Currency must be alphanumeric")
    private String remittanceCurrency;

    @Pattern(regexp = "^$|^[a-zA-Z0-9]{10}$", message = "IE Code must be exactly 10 alphanumeric characters")
    private String ieCode;

    @NotBlank(message = "Remitter Name is mandatory")
    @Size(max = 200, message = "Remitter Name must be max 200 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9\\-_/\\\\.,;:*!#$@+^?\\s]+$",
        message = "Remitter Name contains invalid characters"
    )
    private String remitterName;

    @NotBlank(message = "Remitter Country is mandatory")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Remitter Country must contain alphabets only")
    private String remitterCountry;

    @NotBlank(message = "Purpose of Remittance is mandatory")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_/\\s]+$", message = "Purpose of Remittance contains invalid characters")
    private String purposeOfRemittance;

    @NotBlank(message = "IFSC Code is mandatory")
    @Pattern(regexp = "^[a-zA-Z0-9]{11}$", message = "IFSC Code must be exactly 11 alphanumeric characters")
    private String ifscCode;

    @Pattern(regexp = "^$|^\\d+(\\.\\d{1,4})?$", message = "INR Credit Amount must be numeric")
    private String inrCreditAmount;

    @NotBlank(message = "PAN Number is mandatory")
    @Pattern(regexp = "^[a-zA-Z0-9]{10}$", message = "PAN Number must be exactly 10 alphanumeric characters")
    private String panNumber;

    @Pattern(regexp = "^$|^[a-zA-Z0-9\\-]{1,20}$", message = "Bank Reference Number must be max 20 alphanumeric/hyphen characters")
    private String bankRefNumber;

    @NotBlank(message = "Bank Account Number is mandatory")
    @Pattern(regexp = "^[a-zA-Z0-9\\-]{1,25}$", message = "Bank Account Number must be max 25 alphanumeric/hyphen characters")
    private String bankAccNum;

    @Pattern(regexp = "^$|^\\d{2}/\\d{2}/\\d{4}$", message = "IRM Issue Date must be dd/MM/yyyy")
    private String remittanceIssueDate;
}