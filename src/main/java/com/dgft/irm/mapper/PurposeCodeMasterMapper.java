package com.dgft.irm.mapper;

import com.dgft.irm.dto.request.PurposeCodeMasterRequestDto;
import com.dgft.irm.dto.response.PurposeCodeMasterResponseDto;
import com.dgft.irm.entity.PurposeCodeMaster;

public final class PurposeCodeMasterMapper {
    private PurposeCodeMasterMapper() {}

    public static PurposeCodeMasterResponseDto toResponseDto(PurposeCodeMaster e) {
        if (e == null) return null;
        PurposeCodeMasterResponseDto dto = new PurposeCodeMasterResponseDto();
        dto.setId(e.getId());
        dto.setCode(e.getCode());
        dto.setDescription(e.getDescription());
        dto.setStatus(e.getStatus());
        dto.setPurposeGroup(e.getPurposeGroup());
        dto.setGroupName(e.getGroupName());
        dto.setApplicationType(e.getApplicationType());
        return dto;
    }

    public static PurposeCodeMaster toEntity(PurposeCodeMasterRequestDto dto) {
        PurposeCodeMaster e = new PurposeCodeMaster();
        e.setCode(dto.getCode());
        e.setDescription(dto.getDescription());
        e.setStatus(dto.getStatus());
        e.setPurposeGroup(dto.getPurposeGroup());
        e.setGroupName(dto.getGroupName());
        e.setRemarks(dto.getRemarks());
        e.setApplicationType(dto.getApplicationType());
        return e;
    }
}
