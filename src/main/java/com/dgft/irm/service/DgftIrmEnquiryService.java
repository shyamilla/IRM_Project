 
package com.dgft.irm.service;
 
public interface DgftIrmEnquiryService {
 
    /**
     * Step 4: reviews the DGFT status of IRM batches already pushed to
     * the staging table (Step 3) and reflects confirmed progress onto
     * dgft_irm_master (TRRACS source-of-truth row).
     */
    void enquireIrmStatus();
 
    /**
     * Step 6: reads the terminal outcome Step 5 already wrote to
     * dgft_irm_message_master / dgft_irm_message_detail
     * (MSG_PUSH_FULLY_PROCESSED / MSG_PUSH_PROCESS_FAILED) and reflects
     * the final, per-record PROCESSED/FAILED outcome onto
     * dgft_irm_master (TRRACS source-of-truth row). Makes no further
     * DGFT API call - this is a pure staging-to-master sync.
     */
    void finalizeIrmMasterStatus();
}