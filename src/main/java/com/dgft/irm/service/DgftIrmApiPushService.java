package com.dgft.irm.service;
 
public interface DgftIrmApiPushService {
 
    /**
     * Fetches IRM records pending push from the staging tables and
     * pushes them to the DGFT end (via MockDgftApiClient), then
     * updates DGFT_IRM_MESSAGE_MASTER and DGFT_IRM_MESSAGE_DETAIL
     * with the resulting status.
     */
    void pushIrmDetailsToDgft();
}