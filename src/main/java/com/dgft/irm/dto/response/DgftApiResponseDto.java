package com.dgft.irm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Minimal DGFT API response payload used by schedulers.
 * The real integration can deserialize the DGFT response into this structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DgftApiResponseDto {

    /** e.g. VALIDATED / REJECTED (mock uses these). */
    private String ackStatus;

    /** raw body or message returned by DGFT */
    private String rawBody;

    /** extracted mock transaction ref if any */
    private String dgftTxRef;

    private boolean success;
    private String errorCode;
    private String errorDetails;
    private String responseJson;
}
