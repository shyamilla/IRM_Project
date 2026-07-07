package com.dgft.irm.service;


import java.util.List;

import com.dgft.irm.dto.response.IrmMsgTxStatusLogResponseDto;

public interface IrmMsgTxStatusLogService {

    List<IrmMsgTxStatusLogResponseDto> getByMessageMasterId(String messageMasterId);
}