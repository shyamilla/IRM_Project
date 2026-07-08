package com.dgft.irm.scheduler;
 
import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.RecordResultDto;
 
import java.util.List;
 
/**
 * DGFT "get response status" API contract used by Step 5. Given the
 * unique transaction id of a batch already pushed (Step 3) and the IRM
 * reference numbers reported in that batch, returns DGFT's current
 * review outcome for each record.
 *
 * Contract:
 *   - ApiResponseDto.success = false            -> call/transport failure (retry later)
 *   - ApiResponseDto.success = true, data empty -> DGFT has not reviewed yet
 *   - ApiResponseDto.success = true, data populated -> DGFT has reviewed;
 *     each RecordResultDto.success indicates pass/fail for that record,
 *     RecordResultDto.reasons carries failure detail(s).
 */
public interface DgftIrmProcessedStatusApiClient {
 
    ApiResponseDto<List<RecordResultDto>> getProcessedStatus(String uniqueTxId, List<String> irmRefNumbers);
}