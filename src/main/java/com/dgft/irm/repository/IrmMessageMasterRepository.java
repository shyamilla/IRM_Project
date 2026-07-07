package com.dgft.irm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dgft.irm.entity.IrmMessageMaster;

import java.util.Optional;

public interface IrmMessageMasterRepository extends JpaRepository<IrmMessageMaster, String> {
    Optional<IrmMessageMaster> findByUniqueTxId(String uniqueTxId);
}