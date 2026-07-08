package com.dgft.irm.scheduler;
 
import com.dgft.irm.dto.response.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
 
/**
 * Mock implementation of the DGFT Enquiry API contract, active only
 * under the "mock" profile (application.properties already sets
 * spring.profiles.active=mock). Always returns a validated status
 * with no real network call, so the Step 4 flow can be exercised
 * end-to-end in dev/test.
 *
 * A real implementation can later be added under a different
 * @Profile, implementing the same DgftEnquiryApiClient interface,
 * with no changes needed to DgftIrmEnquiryServiceImpl.
 */
@Slf4j
@Component
@Profile("mock")
public class MockDgftEnquiryApiClient implements DgftEnquiryApiClient {
 
    @Override
    public ApiResponseDto<String> enquireStatus(String uniqueTxId) {
 
        log.info("[MOCK DGFT] Simulating enquiry for uniqueTxId: {}", uniqueTxId);
 
        return ApiResponseDto.ok("IRM batch validated by DGFT (mock enquiry)", uniqueTxId);
    }
}