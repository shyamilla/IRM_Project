package com.dgft.irm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dgft.irm.entity.IrmMasterHis;

import java.util.List;

public interface IrmMasterHisRepository extends JpaRepository<IrmMasterHis, String> {
    List<IrmMasterHis> findByIrmIdOrderByTriggerDateDesc(String irmId);
}