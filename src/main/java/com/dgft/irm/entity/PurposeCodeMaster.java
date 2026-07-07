package com.dgft.irm.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "dgft_purpose_code_master")
@Data
public class PurposeCodeMaster {

    @Id
    @Column(name = "ID", length = 200)
    private String id;

    @Column(name = "CODE", length = 20)
    private String code;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "PURPOSE_GROUP", length = 2)
    private String purposeGroup;

    @Column(name = "GROUP_NAME", length = 50)
    private String groupName;

    @Column(name = "ADDED_BY", length = 50)
    private String addedBy;

    @Column(name = "ADDED_DATE")
    private LocalDateTime addedDate;

    @Column(name = "MODIFIED_BY", length = 50)
    private String modifiedBy;

    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @Column(name = "REMARKS", length = 200)
    private String remarks;

    @Column(name = "APPLICATION_TYPE", length = 4)
    private String applicationType;
}
