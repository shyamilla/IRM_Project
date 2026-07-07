package com.dgft.irm.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class IrmMessageDetailResponseDto {
    private String id;
    private String dgftIrmMsgMasterId;
    private String irmNumber;
    private LocalDate irmIssueDate;
    private String status;
    private String dgftAckStatus;
    private String dgftErrorCodes;
    private String dgftErrorDetails;
    private LocalDateTime addedDate;
}