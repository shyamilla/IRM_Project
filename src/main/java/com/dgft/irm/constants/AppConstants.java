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
}


