package com.dgft.irm.scheduler;
 
import com.dgft.irm.dto.response.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
 
/**
 * Mock implementation of the DGFT API contract, active only under the
 * "mock" profile (see application.properties: spring.profiles.active=mock).
 * Simulates a successful DGFT acknowledgement with no real network call,
 * so the full Step 3 push flow can be exercised end-to-end in dev/test.
 *
 * A real implementation (e.g. RealDgftApiClient) can later be added
 * under a different @Profile, implementing the same DgftApiClient
 * interface, with no changes needed to DgftIrmApiPushServiceImpl.
 */
@Slf4j
@Component
@Profile("mock")
public class MockDgftApiClient implements DgftApiClient {
 
    @Override
    public ApiResponseDto<String> pushIrm(String requestJson) {
 
        log.info("[MOCK DGFT] Simulating IRM push. Payload size: {} chars",
                requestJson == null ? 0 : requestJson.length());
 
        return ApiResponseDto.ok("IRM data validated by DGFT (mock)", requestJson);
    }
}