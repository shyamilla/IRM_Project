package com.dgft.irm.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IrmMsgTxStatusLogResponseDto {
    private String id;
    private String dgftIrmMsgMasterId;
    private String dgftTxStatusJsonObj;
    private LocalDateTime addedDate;
}