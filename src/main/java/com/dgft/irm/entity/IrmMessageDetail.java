package com.dgft.irm.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dgft_irm_message_detail")
@Data
public class IrmMessageDetail {

    @Id
    @Column(name = "ID", length = 200)
    private String id;

    @Column(name = "DGFT_IRM_MSG_MASTER_ID", length = 200)
    private String dgftIrmMsgMasterId;

    @Column(name = "IRM_NUMBER", length = 50)
    private String irmNumber;

    @Column(name = "IRM_ISSUE_DATE")
    private LocalDate irmIssueDate;

    @Column(name = "STATUS", length = 10)
    private String status;

    @Column(name = "DGFT_ACK_STATUS", length = 25)
    private String dgftAckStatus;

    @Column(name = "DGFT_ERROR_CODES", length = 1000)
    private String dgftErrorCodes;

    @Column(name = "DGFT_ERROR_DETAILS", length = 1000)
    private String dgftErrorDetails;

    @Column(name = "ADDED_BY", length = 50)
    private String addedBy;

    @Column(name = "ADDED_DATE")
    private LocalDateTime addedDate;

    @Column(name = "MODIFIED_BY", length = 50)
    private String modifiedBy;

    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;
}
