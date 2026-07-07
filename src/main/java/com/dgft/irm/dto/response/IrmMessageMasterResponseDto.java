package com.dgft.irm.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IrmMessageMasterResponseDto {
    private String id;
    private String uniqueTxId;
    private String dgftAckStatus;
    private String status;
    private String dgftMsgPushError;
    private LocalDateTime addedDate;
    private LocalDateTime dgftPushInitTime;
}