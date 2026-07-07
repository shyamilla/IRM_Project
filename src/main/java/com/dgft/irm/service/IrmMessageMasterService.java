package com.dgft.irm.service;


import java.util.List;

import com.dgft.irm.dto.response.IrmMessageMasterResponseDto;

public interface IrmMessageMasterService {

    IrmMessageMasterResponseDto getById(String id);

    List<IrmMessageMasterResponseDto> getAll();
}