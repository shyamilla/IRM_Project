package com.dgft.irm.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "dgft_irm_message_master")
@Data
public class IrmMessageMaster {

    @Id
    @Column(name = "ID", length = 200)
    private String id;

    @Column(name = "UNIQUE_TX_ID", length = 25)
    private String uniqueTxId;

    @Lob
    @Column(name = "REQUEST_JSON_OBJ")
    private String requestJsonObj;

    @Lob
    @Column(name = "RESPONSE_JSON_OBJ")
    private String responseJsonObj;

    @Column(name = "DGFT_ACK_STATUS", length = 25)
    private String dgftAckStatus;

    @Column(name = "STATUS", length = 50)
    private String status;

    @Column(name = "DGFT_MSG_PUSH_ERROR", length = 1000)
    private String dgftMsgPushError;

    @Lob
    @Column(name = "DGFT_LAST_TX_STATUS_JSON_OBJ")
    private String dgftLastTxStatusJsonObj;

    @Column(name = "ADDED_BY", length = 50)
    private String addedBy;

    @Column(name = "ADDED_DATE")
    private LocalDateTime addedDate;

    @Column(name = "MODIFIED_BY", length = 50)
    private String modifiedBy;

    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @Column(name = "DGFT_PUSH_INIT_TIME")
    private LocalDateTime dgftPushInitTime;

    @Column(name = "DGFT_LAST_TX_STATUS_INIT_TIME")
    private LocalDateTime dgftLastTxStatusInitTime;
}

