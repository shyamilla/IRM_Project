package com.dgft.irm.scheduler;

import com.dgft.irm.dto.response.ApiResponseDto;

/**
 * DGFT API contract used by Step 3.
 */
public interface DgftApiClient {

    ApiResponseDto<String> pushIrm(String requestJson);
}

