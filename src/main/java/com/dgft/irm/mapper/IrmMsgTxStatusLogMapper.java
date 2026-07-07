package com.dgft.irm.mapper;

import com.dgft.irm.dto.response.IrmMsgTxStatusLogResponseDto;
import com.dgft.irm.entity.IrmMsgTxStatusLog;

public final class IrmMsgTxStatusLogMapper {
    private IrmMsgTxStatusLogMapper() {}

    public static IrmMsgTxStatusLogResponseDto toResponseDto(IrmMsgTxStatusLog e) {
        if (e == null) return null;
        IrmMsgTxStatusLogResponseDto dto = new IrmMsgTxStatusLogResponseDto();
        dto.setId(e.getId());
        dto.setDgftIrmMsgMasterId(e.getDgftIrmMsgMasterId());
        dto.setDgftTxStatusJsonObj(e.getDgftTxStatusJsonObj());
        dto.setAddedDate(e.getAddedDate());
        return dto;
    }
}
