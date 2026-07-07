package com.dgft.irm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dgft.irm.entity.PurposeCodeMaster;

import java.util.Optional;

public interface PurposeCodeMasterRepository extends JpaRepository<PurposeCodeMaster, String> {
    boolean existsByCodeAndStatus(String code, String status);
    Optional<PurposeCodeMaster> findByCode(String code);
}