package com.dgft.irm.scheduler;
 
import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;
import com.dgft.irm.dto.response.DgftApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
 
/**
 * Mock client that simulates the DGFT external API for local/dev/testing.
 * No real network call is made - it always returns a successful
 * "Validated" acknowledgement so the push flow can be exercised
 * end-to-end without depending on the actual DGFT environment.
 */
@Slf4j
@Component
public class MockDgftApiClient {
 
    public DgftApiResponseDto pushIrmDetails(DgftIrmOutboundRequestDto requestDto) {
 
        log.info("[MOCK DGFT] Simulating IRM push for uniqueTxId: {}", requestDto.getUniqueTxId());
 
        DgftApiResponseDto response = new DgftApiResponseDto();
        response.setSuccess(true);
        response.setAckStatus("Validated");
        response.setErrorCode(null);
        response.setErrorDetails(null);
        response.setResponseJson(
                "{\"txId\":\"" + requestDto.getUniqueTxId() + "\",\"ackStatus\":\"Validated\"}"
        );
 
        return response;
    }
}