package com.dgft.irm.service;
 
public interface DgftIrmPushService {
 
    /**
     * Processes exactly ONE batch (up to dgft.scheduler.irm-push.batch-size,
     * default 20) of fresh DGFT_IRM_MASTER records into the staging
     * tables, as a single DB transaction: one DGFT_IRM_MESSAGE_MASTER
     * row + one DGFT_IRM_MESSAGE_DETAIL row per record in the batch.
     *
     * @return true  - a non-empty batch was found and staged; the caller
     *                 should call this again to check/process further
     *                 pending records.
     *         false - there were no fresh records left to stage.
     */
    boolean pushNextBatchToDgft();
}