package com.dgft.irm.service;

import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;

public interface DgftMockApiService {

    boolean pushIrmToDgft(DgftIrmOutboundRequestDto request);
}