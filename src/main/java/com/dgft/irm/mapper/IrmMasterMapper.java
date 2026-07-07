package com.dgft.irm.mapper;

import com.dgft.irm.dto.response.IrmMasterResponseDto;
import com.dgft.irm.entity.IrmMaster;

public final class IrmMasterMapper {
    private IrmMasterMapper() {}

    public static IrmMasterResponseDto toResponseDto(IrmMaster e) {
        if (e == null) return null;
        IrmMasterResponseDto dto = new IrmMasterResponseDto();
        dto.setId(e.getId());
        dto.setIrmNumber(e.getIrmNumber());
        dto.setRemittanceAmount(e.getRemittanceAmount());
        dto.setRemittanceDate(e.getRemittanceDate());
        dto.setAdCode(e.getAdCode());
        dto.setCurrency(e.getCurrency());
        dto.setIeCode(e.getIeCode());
        dto.setRemitterName(e.getRemitterName());
        dto.setRemitterCountry(e.getRemitterCountry());
        dto.setPurposeOfRemittance(e.getPurposeOfRemittance());
        dto.setIfscCode(e.getIfscCode());
        dto.setInrCreditAmount(e.getInrCreditAmount());
        dto.setPanNumber(e.getPanNumber());
        dto.setBankReferenceNumber(e.getBankReferenceNumber());
        dto.setBankAccountNumber(e.getBankAccountNumber());
        dto.setIrmIssueDate(e.getIrmIssueDate());
        dto.setStatus(e.getStatus());
        dto.setFlag(e.getFlag());
        dto.setDgftFlag(e.getDgftFlag());
        dto.setDgftStatus(e.getDgftStatus());
        dto.setInboundFileName(e.getInboundFileName());
        dto.setAddedDate(e.getAddedDate());
        return dto;
    }
}
