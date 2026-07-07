package com.dgft.irm.service;


import java.util.List;

import com.dgft.irm.dto.response.IrmMasterHisResponseDto;

public interface IrmMasterHisService {

    List<IrmMasterHisResponseDto> getHistoryByIrmId(String irmId);
}