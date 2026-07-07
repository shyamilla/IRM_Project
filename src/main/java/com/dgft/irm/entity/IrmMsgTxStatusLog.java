package com.dgft.irm.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "dgft_irm_msg_tx_status_log")
@Data
public class IrmMsgTxStatusLog {

    @Id
    @Column(name = "ID", length = 200)
    private String id;

    @Column(name = "DGFT_IRM_MSG_MASTER_ID", length = 200)
    private String dgftIrmMsgMasterId;

    @Lob
    @Column(name = "DGFT_TX_STATUS_JSON_OBJ")
    private String dgftTxStatusJsonObj;

    @Column(name = "ADDED_BY", length = 50)
    private String addedBy;

    @Column(name = "ADDED_DATE")
    private LocalDateTime addedDate;
}
