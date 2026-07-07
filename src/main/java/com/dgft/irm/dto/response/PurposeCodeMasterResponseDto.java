package com.dgft.irm.dto.response;

import lombok.Data;

@Data
public class PurposeCodeMasterResponseDto {
    private String id;
    private String code;
    private String description;
    private String status;
    private String purposeGroup;
    private String groupName;
    private String applicationType;
}
