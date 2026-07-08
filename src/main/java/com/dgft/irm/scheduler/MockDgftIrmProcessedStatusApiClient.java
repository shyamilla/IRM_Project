package com.dgft.irm.scheduler;
 
import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.RecordResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
 
import java.util.List;
import java.util.stream.Collectors;
 
/**
 * Mock implementation active only under the "mock" profile. Simulates DGFT
 * having already reviewed every record and returning a success outcome for
 * each, so the full Step 5 flow can be exercised end-to-end in dev/test.
 *
 * A real implementation (e.g. RealDgftIrmProcessedStatusApiClient) can later
 * be added under a different @Profile, with no changes needed to
 * DgftIrmProcessedStatusServiceImpl.
 */
@Slf4j
@Component
@Profile("mock")
public class MockDgftIrmProcessedStatusApiClient implements DgftIrmProcessedStatusApiClient {
 
    @Override
    public ApiResponseDto<List<RecordResultDto>> getProcessedStatus(String uniqueTxId, List<String> irmRefNumbers) {
 
        log.info("[MOCK DGFT] Simulating processed-status check for uniqueTxId [{}], {} record(s).",
                uniqueTxId, irmRefNumbers == null ? 0 : irmRefNumbers.size());
 
        List<RecordResultDto> results = (irmRefNumbers == null ? List.<String>of() : irmRefNumbers).stream()
                .map(irmRefNumber -> new RecordResultDto(0, irmRefNumber, true, "MOCK-ACK-" + irmRefNumber, null))
                .collect(Collectors.toList());
 
        return ApiResponseDto.ok("DGFT processed-status check successful (mock)", results);
    }
}