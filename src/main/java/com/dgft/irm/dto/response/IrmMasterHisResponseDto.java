package com.dgft.irm.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IrmMasterHisResponseDto {
    private String id;
    private String irmId;
    private String irmNumber;
    private String status;
    private String flag;
    private String dgftFlag;
    private String dgftStatus;
    private String triggerStatus;
    private LocalDateTime triggerDate;
}
