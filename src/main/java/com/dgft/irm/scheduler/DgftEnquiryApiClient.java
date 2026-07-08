package com.dgft.irm.scheduler;
 
import com.dgft.irm.dto.response.ApiResponseDto;
 
/**
 * DGFT Enquiry API contract used by Step 4. Given the unique
 * transaction id of a previously pushed batch, returns the current
 * status of that batch at the DGFT end.
 */
public interface DgftEnquiryApiClient {
 
    ApiResponseDto<String> enquireStatus(String uniqueTxId);
}