package com.dgft.irm.service;


import java.util.List;

import com.dgft.irm.dto.response.IrmMessageDetailResponseDto;

public interface IrmMessageDetailService {

    List<IrmMessageDetailResponseDto> getByMessageMasterId(String messageMasterId);

    List<IrmMessageDetailResponseDto> getByIrmNumber(String irmNumber);
}