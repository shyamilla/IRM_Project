package com.dgft.irm.constants;

public final class AppConstants {
    private AppConstants() {}

    // dgft_irm_master values used by Step 1 of the "new records" flow
    public static final String FLAG_NEW_WITH_WORKFLOW = "N";
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String DGFT_FLAG_FRESH = "F";
    public static final String DGFT_STATUS_AWAITING_REQUEST_INITIATED = "Awaiting request initiated";

    // dgft_irm_master_his trigger status values
    public static final String TRIGGER_STATUS_NEW_RECORD_INSERTED = "NEW_RECORD_INSERTED";

    public static final String PURPOSE_CODE_STATUS_ACTIVE = "ACTIVE";

    public static final String ACK_STATUS_PASS = "PASS";
    public static final String ACK_STATUS_FAIL = "FAIL";
}
