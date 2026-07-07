package com.dgft.irm.serviceimpl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.dgft.irm.dto.response.IrmMessageMasterResponseDto;
import com.dgft.irm.exception.ResourceNotFoundException;
import com.dgft.irm.mapper.IrmMessageMasterMapper;
import com.dgft.irm.repository.IrmMessageMasterRepository;
import com.dgft.irm.service.IrmMessageMasterService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IrmMessageMasterServiceImpl implements IrmMessageMasterService {

    private final IrmMessageMasterRepository irmMessageMasterRepository;

    @Override
    public IrmMessageMasterResponseDto getById(String id) {
        return irmMessageMasterRepository.findById(id)
                .map(IrmMessageMasterMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("IRM Message Master not found for id: " + id));
    }

    @Override
    public List<IrmMessageMasterResponseDto> getAll() {
        return irmMessageMasterRepository.findAll()
                .stream()
                .map(IrmMessageMasterMapper::toResponseDto)
                .toList();
    }
}