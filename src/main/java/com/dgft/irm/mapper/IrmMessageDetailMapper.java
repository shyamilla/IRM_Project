package com.dgft.irm.mapper;

import com.dgft.irm.dto.response.IrmMessageDetailResponseDto;
import com.dgft.irm.entity.IrmMessageDetail;

public final class IrmMessageDetailMapper {
    private IrmMessageDetailMapper() {}

    public static IrmMessageDetailResponseDto toResponseDto(IrmMessageDetail e) {
        if (e == null) return null;
        IrmMessageDetailResponseDto dto = new IrmMessageDetailResponseDto();
        dto.setId(e.getId());
        dto.setDgftIrmMsgMasterId(e.getDgftIrmMsgMasterId());
        dto.setIrmNumber(e.getIrmNumber());
        dto.setIrmIssueDate(e.getIrmIssueDate());
        dto.setStatus(e.getStatus());
        dto.setDgftAckStatus(e.getDgftAckStatus());
        dto.setDgftErrorCodes(e.getDgftErrorCodes());
        dto.setDgftErrorDetails(e.getDgftErrorDetails());
        dto.setAddedDate(e.getAddedDate());
        return dto;
    }
}