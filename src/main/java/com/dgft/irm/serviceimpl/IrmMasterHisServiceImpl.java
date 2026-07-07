package com.dgft.irm.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.dgft.irm.dto.response.IrmMasterHisResponseDto;
import com.dgft.irm.mapper.IrmMasterHisMapper;
import com.dgft.irm.repository.IrmMasterHisRepository;
import com.dgft.irm.service.IrmMasterHisService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IrmMasterHisServiceImpl implements IrmMasterHisService {

    private final IrmMasterHisRepository irmMasterHisRepository;

    @Override
    public List<IrmMasterHisResponseDto> getHistoryByIrmId(String irmId) {
        return irmMasterHisRepository.findByIrmIdOrderByTriggerDateDesc(irmId)
                .stream()
                .map(IrmMasterHisMapper::toResponseDto)
                .toList();
    }
}