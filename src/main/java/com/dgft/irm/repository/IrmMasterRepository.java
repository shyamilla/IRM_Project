package com.dgft.irm.repository;
 
import java.util.List;
import java.util.Optional;
 
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.dgft.irm.entity.IrmMaster;
 
public interface IrmMasterRepository extends JpaRepository<IrmMaster, String> {
    boolean existsByIrmNumber(String irmNumber);
    Optional<IrmMaster> findByIrmNumber(String irmNumber);
    List<IrmMaster> findByStatus(String status);
 
    // Pulls up to `pageable`'s page size, oldest eligible first.
    // Because it's ordered ascending and capped, element [0] of the
    // result (when non-empty) is the single oldest eligible record
    // system-wide - used to decide whether a partial batch has been
    // waiting long enough to flush.
    List<IrmMaster> findByDgftFlagAndDgftStatusOrderByAddedDateAsc(
            String dgftFlag,
            String dgftStatus,
            Pageable pageable
    );
}