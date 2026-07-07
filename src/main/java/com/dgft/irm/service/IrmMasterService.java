package com.dgft.irm.service;

import java.util.List;

import com.dgft.irm.dto.response.IrmMasterResponseDto;


public interface IrmMasterService {
    IrmMasterResponseDto getById(String id);
    IrmMasterResponseDto getByIrmNumber(String irmNumber);
    List<IrmMasterResponseDto> getAll();
    List<IrmMasterResponseDto> getByStatus(String status);
}
