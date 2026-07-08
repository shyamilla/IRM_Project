package com.dgft.irm.constants;

public final class AppConstants {
    private AppConstants() {}

    // dgft_irm_master values used by Step 1 of the "new records" flow
    public static final String FLAG_NEW_WITH_WORKFLOW = "N";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String DGFT_FLAG_FRESH = "F";
    public static final String DGFT_STATUS_AWAITING_REQUEST_INITIATED = "Awaiting request initiated";

    // dgft_irm_master values used by Step 2 of    the "new records" flow
    // (DGFT IRM Push Service Scheduler - push to intermediate staging table)
    public static final String FLAG_PUSHED_TO_STAGING = "P";
    public static final String DGFT_STATUS_REQUEST_INITIATED = "Request initiated";

    // dgft_irm_master values used by Step 3+ for NEW flow
    public static final String DGFT_STATUS_PENDING = "Pending";
    public static final String DGFT_STATUS_VALIDATED = "Validated";
    public static final String DGFT_STATUS_PROCESSED = "Processed";
    public static final String DGFT_STATUS_FAILED = "Failed";

    // dgft_irm_message_master values used by Step 2
    public static final String MSG_MASTER_STATUS_NEW = "MSG_PUSH_NEW";
    public static final String MSG_MASTER_ACK_STATUS_REQUEST_INITIATED = "REQUEST_INITIATED";

    // dgft_irm_message_master values used by Step 3 for NEW flow
    public static final String MSG_PUSH_SUCCESS = "MSG_PUSH_SUCCESS";
    public static final String MSG_PUSH_PROCESS_FAILED = "MSG_PUSH_PROCESS_FAILED";

    // dgft_irm_message_detail values used by Step 2+3
    public static final String MSG_DETAIL_STATUS_NEW = "NEW";
    public static final String MSG_DETAIL_STATUS_PENDING = "PENDING";

    // dgft_irm_master_his trigger status values
    public static final String TRIGGER_STATUS_NEW_RECORD_INSERTED = "NEW_RECORD_INSERTED";
    public static final String TRIGGER_STATUS_PUSHED_TO_STAGING = "PUSHED_TO_STAGING";

    public static final String PURPOSE_CODE_STATUS_ACTIVE = "ACTIVE";

    public static final String ACK_STATUS_PASS = "PASS";
    public static final String ACK_STATUS_FAIL = "FAIL";


    // dgft_irm_master_his trigger status value used by Step 4 (Enquiry scheduler)
    public static final String TRIGGER_STATUS_ENQUIRY_VALIDATED = "ENQUIRY_VALIDATED";


    
    // dgft_irm_message_master / dgft_irm_message_detail values used by Step 5
    // (DGFT API IRM Processed Status Service Scheduler)
    public static final String MSG_PUSH_PENDING_PROCESS = "MSG_PUSH_PENDING_PROCESS";
    public static final String MSG_PUSH_FULLY_PROCESSED = "MSG_PUSH_FULLY_PROCESSED";
 
    // NOTE: spec explicitly gives this as all-caps "FAILED", distinct from
    // DGFT_STATUS_FAILED ("Failed") used elsewhere - verify with source doc.
    public static final String MSG_MASTER_ACK_STATUS_FAILED_ALL = "FAILED";
 
    public static final String MSG_DETAIL_STATUS_PROCESSED = "PROCESSED";
    public static final String MSG_DETAIL_STATUS_FAILED = "FAILED";
    public static final String MSG_DETAIL_ACK_STATUS_ERRORED = "Errored";
 
    // Reused existing constants (no change needed):
    //   DGFT_STATUS_VALIDATED  = "Validated"   -> used for "pending review" ack status
    //   DGFT_STATUS_PROCESSED  = "Processed"   -> used for "fully processed" ack status
    //   MSG_PUSH_SUCCESS       = "MSG_PUSH_SUCCESS"        -> Step 3 output picked up by Step 5
    //   MSG_PUSH_PROCESS_FAILED = "MSG_PUSH_PROCESS_FAILED" -> reused as terminal all-failed master status
    //   MSG_DETAIL_STATUS_PENDING = "PENDING"  -> reused for still-pending detail rows



    // dgft_irm_master values used by Step 6 (final status sync from staging)
    public static final String FLAG_PROCESSED_ACTIVE = "A";
    public static final String FLAG_PROCESSED_FAILED = "F";
    public static final String TRIGGER_STATUS_FINAL_STATUS_SYNCED = "FINAL_STATUS_SYNCED";
 
    // Reused existing constants (no change needed):
    //   STATUS_ACTIVE               = "ACTIVE"
    //   DGFT_FLAG_FRESH             = "F"
    //   DGFT_STATUS_PROCESSED       = "Processed"   -> mapped to spec's "PROCESSED"
    //   DGFT_STATUS_FAILED          = "Failed"      -> mapped to spec's "FAILED"
    //   MSG_PUSH_FULLY_PROCESSED    = "MSG_PUSH_FULLY_PROCESSED"  (Step 5 constant)
    //   MSG_PUSH_PROCESS_FAILED     = "MSG_PUSH_PROCESS_FAILED"   (Step 5 constant)
    //   MSG_DETAIL_STATUS_PROCESSED = "PROCESSED"                 (Step 5 constant)
    //   MSG_DETAIL_STATUS_FAILED    = "FAILED"                    (Step 5 constant)
}


