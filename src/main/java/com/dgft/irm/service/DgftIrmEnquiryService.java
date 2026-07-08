package com.dgft.irm.service;
 
public interface DgftIrmEnquiryService {
 
    /**
     * Reviews the DGFT status of IRM batches already pushed to the
     * staging table (Step 3) and reflects confirmed progress onto
     * dgft_irm_master (TRRACS source-of-truth row).
     */
    void enquireIrmStatus();
}