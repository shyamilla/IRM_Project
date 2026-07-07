package com.dgft.irm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dgft.irm.entity.IrmMessageDetail;

import java.util.List;

public interface IrmMessageDetailRepository extends JpaRepository<IrmMessageDetail, String> {
    List<IrmMessageDetail> findByDgftIrmMsgMasterId(String dgftIrmMsgMasterId);
    List<IrmMessageDetail> findByIrmNumber(String irmNumber);
}
