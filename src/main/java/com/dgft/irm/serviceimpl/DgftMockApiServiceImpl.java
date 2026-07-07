package com.dgft.irm.serviceimpl;

import org.springframework.stereotype.Service;

import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;
import com.dgft.irm.service.DgftMockApiService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DgftMockApiServiceImpl implements DgftMockApiService {

    @Override
    public boolean pushIrmToDgft(DgftIrmOutboundRequestDto request) {

        log.info("Mock DGFT API called.");
        log.info("UniqueTxId: {}", request.getUniqueTxId());
        log.info("Total IRM Records Sent: {}", request.getIrmList().size());

        return true;
    }
}
