package com.dgft.irm.serviceimpl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.dgft.irm.dto.response.IrmMsgTxStatusLogResponseDto;
import com.dgft.irm.mapper.IrmMsgTxStatusLogMapper;
import com.dgft.irm.repository.IrmMsgTxStatusLogRepository;
import com.dgft.irm.service.IrmMsgTxStatusLogService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IrmMsgTxStatusLogServiceImpl implements IrmMsgTxStatusLogService {

    private final IrmMsgTxStatusLogRepository irmMsgTxStatusLogRepository;

    @Override
    public List<IrmMsgTxStatusLogResponseDto> getByMessageMasterId(String messageMasterId) {
        return irmMsgTxStatusLogRepository
                .findByDgftIrmMsgMasterIdOrderByAddedDateDesc(messageMasterId)
                .stream()
                .map(IrmMsgTxStatusLogMapper::toResponseDto)
                .toList();
    }
}