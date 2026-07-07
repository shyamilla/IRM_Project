package com.dgft.irm.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import com.dgft.irm.dto.request.PurposeCodeMasterRequestDto;
import com.dgft.irm.dto.response.PurposeCodeMasterResponseDto;
import com.dgft.irm.entity.PurposeCodeMaster;
import com.dgft.irm.exception.ResourceNotFoundException;
import com.dgft.irm.mapper.PurposeCodeMasterMapper;
import com.dgft.irm.repository.PurposeCodeMasterRepository;
import com.dgft.irm.service.PurposeCodeMasterService;
import com.dgft.irm.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurposeCodeMasterServiceImpl implements PurposeCodeMasterService {

    private final PurposeCodeMasterRepository purposeCodeMasterRepository;

    @Override
    public PurposeCodeMasterResponseDto create(PurposeCodeMasterRequestDto request) {

        PurposeCodeMaster entity = PurposeCodeMasterMapper.toEntity(request);

        entity.setId(IdGenerator.next());
        entity.setAddedDate(LocalDateTime.now());

        if (entity.getStatus() == null || entity.getStatus().isBlank()) {
            entity.setStatus("ACTIVE");
        }

        PurposeCodeMaster savedEntity = purposeCodeMasterRepository.save(entity);

        return PurposeCodeMasterMapper.toResponseDto(savedEntity);
    }

    @Override
    public PurposeCodeMasterResponseDto getById(String id) {
        return purposeCodeMasterRepository.findById(id)
                .map(PurposeCodeMasterMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose code not found for id: " + id));
    }

    @Override
    public List<PurposeCodeMasterResponseDto> getAll() {
        return purposeCodeMasterRepository.findAll()
                .stream()
                .map(PurposeCodeMasterMapper::toResponseDto)
                .toList();
    }

    @Override
    public PurposeCodeMasterResponseDto update(String id, PurposeCodeMasterRequestDto request) {

        PurposeCodeMaster entity = purposeCodeMasterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purpose code not found for id: " + id));

        entity.setCode(request.getCode());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setPurposeGroup(request.getPurposeGroup());
        entity.setGroupName(request.getGroupName());
        entity.setRemarks(request.getRemarks());
        entity.setApplicationType(request.getApplicationType());
        entity.setModifiedDate(LocalDateTime.now());

        PurposeCodeMaster updatedEntity = purposeCodeMasterRepository.save(entity);

        return PurposeCodeMasterMapper.toResponseDto(updatedEntity);
    }

    @Override
    public void delete(String id) {

        if (!purposeCodeMasterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Purpose code not found for id: " + id);
        }

        purposeCodeMasterRepository.deleteById(id);
    }
}