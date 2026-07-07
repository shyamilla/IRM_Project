package com.dgft.irm.serviceimpl;

import com.dgft.irm.dto.response.IrmMasterResponseDto;
import com.dgft.irm.exception.ResourceNotFoundException;
import com.dgft.irm.mapper.IrmMasterMapper;
import com.dgft.irm.repository.IrmMasterRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IrmMasterServiceImpl implements IrmMasterService {

    private final IrmMasterRepository irmMasterRepository;

    @Override
    public IrmMasterResponseDto getById(String id) {
        return irmMasterRepository.findById(id)
                .map(IrmMasterMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("IRM Master not found for id: " + id));
    }

    @Override
    public IrmMasterResponseDto getByIrmNumber(String irmNumber) {
        return irmMasterRepository.findByIrmNumber(irmNumber)
                .map(IrmMasterMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("IRM Master not found for IRM number: " + irmNumber));
    }

    @Override
    public List<IrmMasterResponseDto> getAll() {
        return irmMasterRepository.findAll()
                .stream()
                .map(IrmMasterMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<IrmMasterResponseDto> getByStatus(String status) {
        return irmMasterRepository.findByStatus(status)
                .stream()
                .map(IrmMasterMapper::toResponseDto)
                .toList();
    }
}