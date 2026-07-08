package com.dgft.irm.service;

import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;

public interface DgftMessageGenerationService {

  // Returns the NEXT batch (size = dgft.scheduler.irm-push.batch-size)
    // of fresh IRM records as one outbound request. irmList is empty
    // when there is nothing left to stage.
    DgftIrmOutboundRequestDto generateFreshIrmJson();
}