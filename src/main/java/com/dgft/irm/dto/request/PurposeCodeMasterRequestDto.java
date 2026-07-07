package com.dgft.irm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Purpose Code is mandatory.
//  Other fields are optional and represent descriptive metadata
//  associated with the purpose code.
@Data
public class PurposeCodeMasterRequestDto {
    @NotBlank
    private String code;
    private String description;
    private String status;
    private String purposeGroup;
    private String groupName;
    private String remarks;
    private String applicationType;
}
