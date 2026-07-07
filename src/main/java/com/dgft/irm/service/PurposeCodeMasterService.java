package com.dgft.irm.service;


import java.util.List;

import com.dgft.irm.dto.request.PurposeCodeMasterRequestDto;
import com.dgft.irm.dto.response.PurposeCodeMasterResponseDto;

public interface PurposeCodeMasterService {

    PurposeCodeMasterResponseDto create(PurposeCodeMasterRequestDto request);

    PurposeCodeMasterResponseDto getById(String id);

    List<PurposeCodeMasterResponseDto> getAll();

    PurposeCodeMasterResponseDto update(String id, PurposeCodeMasterRequestDto request);

    void delete(String id);
}