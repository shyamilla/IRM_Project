package com.dgft.irm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dgft.irm.entity.IrmMsgTxStatusLog;

import java.util.List;

public interface IrmMsgTxStatusLogRepository extends JpaRepository<IrmMsgTxStatusLog, String> {
    List<IrmMsgTxStatusLog> findByDgftIrmMsgMasterIdOrderByAddedDateDesc(String dgftIrmMsgMasterId);
}
