package com.dgft.irm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dgft.irm.entity.IrmMaster;

import java.util.List;
import java.util.Optional;

public interface IrmMasterRepository extends JpaRepository<IrmMaster, String> {
    boolean existsByIrmNumber(String irmNumber);
    Optional<IrmMaster> findByIrmNumber(String irmNumber);
    List<IrmMaster> findByStatus(String status);

    List<IrmMaster> findTop20ByDgftFlagAndDgftStatusOrderByAddedDateAsc(
        String dgftFlag,
        String dgftStatus
);
}
