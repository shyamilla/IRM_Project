package com.dgft.irm.serviceimpl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.dgft.irm.dto.response.IrmMessageDetailResponseDto;
import com.dgft.irm.mapper.IrmMessageDetailMapper;
import com.dgft.irm.repository.IrmMessageDetailRepository;
import com.dgft.irm.service.IrmMessageDetailService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IrmMessageDetailServiceImpl implements IrmMessageDetailService {

    private final IrmMessageDetailRepository irmMessageDetailRepository;

    @Override
    public List<IrmMessageDetailResponseDto> getByMessageMasterId(String messageMasterId) {
        return irmMessageDetailRepository.findByDgftIrmMsgMasterId(messageMasterId)
                .stream()
                .map(IrmMessageDetailMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<IrmMessageDetailResponseDto> getByIrmNumber(String irmNumber) {
        return irmMessageDetailRepository.findByIrmNumber(irmNumber)
                .stream()
                .map(IrmMessageDetailMapper::toResponseDto)
                .toList();
    }
}