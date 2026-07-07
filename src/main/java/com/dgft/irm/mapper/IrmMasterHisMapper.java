package com.dgft.irm.mapper;

import com.dgft.irm.dto.response.IrmMasterHisResponseDto;
import com.dgft.irm.entity.IrmMaster;
import com.dgft.irm.entity.IrmMasterHis;

public final class IrmMasterHisMapper {
    private IrmMasterHisMapper() {}

    public static IrmMasterHisResponseDto toResponseDto(IrmMasterHis e) {
        if (e == null) return null;
        IrmMasterHisResponseDto dto = new IrmMasterHisResponseDto();
        dto.setId(e.getId());
        dto.setIrmId(e.getIrmId());
        dto.setIrmNumber(e.getIrmNumber());
        dto.setStatus(e.getStatus());
        dto.setFlag(e.getFlag());
        dto.setDgftFlag(e.getDgftFlag());
        dto.setDgftStatus(e.getDgftStatus());
        dto.setTriggerStatus(e.getTriggerStatus());
        dto.setTriggerDate(e.getTriggerDate());
        return dto;
    }

    /** Builds a history snapshot from the current state of an IrmMaster row. */
    public static IrmMasterHis fromMaster(IrmMaster m, String triggerStatus, String id, java.time.LocalDateTime now) {
        IrmMasterHis h = new IrmMasterHis();
        h.setId(id);
        h.setTriggerDate(now);
        h.setIrmId(m.getId());
        h.setIrmNumber(m.getIrmNumber());
        h.setRemittanceAmount(m.getRemittanceAmount());
        h.setRemittanceDate(m.getRemittanceDate());
        h.setAdCodeId(m.getAdCodeId());
        h.setAdCode(m.getAdCode());
        h.setRemittanceCurrencyId(m.getRemittanceCurrencyId());
        h.setCurrency(m.getCurrency());
        h.setAddedBy(m.getAddedBy());
        h.setAddedDate(m.getAddedDate());
        h.setModifiedBy(m.getModifiedBy());
        h.setModifiedDate(m.getModifiedDate());
        h.setIeCode(m.getIeCode());
        h.setIeName(m.getIeName());
        h.setRemitterName(m.getRemitterName());
        h.setRemitterCountryId(m.getRemitterCountryId());
        h.setRemitterCountry(m.getRemitterCountry());
        h.setSwiftOtherBankRefNumber(m.getSwiftOtherBankRefNumber());
        h.setPurposeOfRemittance(m.getPurposeOfRemittance());
        h.setIfscCode(m.getIfscCode());
        h.setInrCreditAmount(m.getInrCreditAmount());
        h.setExchangeRate(m.getExchangeRate());
        h.setPanNumber(m.getPanNumber());
        h.setModeOfPayment(m.getModeOfPayment());
        h.setStatus(m.getStatus());
        h.setRemarks(m.getRemarks());
        h.setInboundFileName(m.getInboundFileName());
        h.setFlag(m.getFlag());
        h.setDgftFlag(m.getDgftFlag());
        h.setErrorCodes(m.getErrorCodes());
        h.setCheckerRemarks(m.getCheckerRemarks());
        h.setBankUniqueTransactionId(m.getBankUniqueTransactionId());
        h.setBankReferenceNumber(m.getBankReferenceNumber());
        h.setIrmIssueDate(m.getIrmIssueDate());
        h.setRemittanceType(m.getRemittanceType());
        h.setBankAccountNumber(m.getBankAccountNumber());
        h.setDgftStatus(m.getDgftStatus());
        h.setProcessStatus(m.getProcessStatus());
        h.setMasterDetailStatus(m.getMasterDetailStatus());
        h.setTriggerStatus(triggerStatus);
        return h;
    }
}

