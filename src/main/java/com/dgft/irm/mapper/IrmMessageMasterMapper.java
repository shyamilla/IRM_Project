package com.dgft.irm.mapper;

import com.dgft.irm.dto.response.IrmMessageMasterResponseDto;
import com.dgft.irm.entity.IrmMessageMaster;

public final class IrmMessageMasterMapper {
    private IrmMessageMasterMapper() {}

    public static IrmMessageMasterResponseDto toResponseDto(IrmMessageMaster e) {
        if (e == null) return null;
        IrmMessageMasterResponseDto dto = new IrmMessageMasterResponseDto();
        dto.setId(e.getId());
        dto.setUniqueTxId(e.getUniqueTxId());
        dto.setDgftAckStatus(e.getDgftAckStatus());
        dto.setStatus(e.getStatus());
        dto.setDgftMsgPushError(e.getDgftMsgPushError());
        dto.setAddedDate(e.getAddedDate());
        dto.setDgftPushInitTime(e.getDgftPushInitTime());
        return dto;
    }
}
