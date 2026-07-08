package com.dgft.irm.service;
 
public interface DgftIrmProcessedStatusService {
 
    /**
     * Checks DGFT's review outcome for IRM batches already pushed via API
     * (Step 3) and updates the staging tables (DGFT_IRM_MESSAGE_MASTER /
     * DGFT_IRM_MESSAGE_DETAIL) to reflect Pending / Processed / Failed
     * outcomes. Does not touch dgft_irm_master.
     */
    void checkProcessedStatus();
}