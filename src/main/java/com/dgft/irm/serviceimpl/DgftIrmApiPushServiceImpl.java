package com.dgft.irm.serviceimpl;
 
import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;
import com.dgft.irm.dto.response.DgftApiResponseDto;
import com.dgft.irm.entity.IrmMessageDetail;
import com.dgft.irm.entity.IrmMessageMaster;
import com.dgft.irm.repository.IrmMessageDetailRepository;
import com.dgft.irm.repository.IrmMessageMasterRepository;
import com.dgft.irm.scheduler.MockDgftApiClient;
import com.dgft.irm.service.DgftIrmApiPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Slf4j
@Service
@RequiredArgsConstructor
public class DgftIrmApiPushServiceImpl implements DgftIrmApiPushService {
 
    // Status of a master record that is waiting to be pushed to DGFT.
    // Adjust to match your actual staging table convention.
    private static final String STATUS_PENDING_PUSH = "PENDING_PUSH";
    private static final String STATUS_PUSH_SUCCESS = "MSG_PUSH_SUCCESS";
    private static final String ACK_STATUS_VALIDATED = "Validated";
    private static final String DETAIL_STATUS_PENDING = "PENDING";
    private static final String JOB_USER = "SYSTEM";
 
    private final IrmMessageMasterRepository irmMessageMasterRepository;
    private final IrmMessageDetailRepository irmMessageDetailRepository;
    private final MockDgftApiClient mockDgftApiClient;
 
    @Override
    @Transactional
    public void pushIrmDetailsToDgft() {
 
        List<IrmMessageMaster> pendingMasters =
                irmMessageMasterRepository.findByStatus(STATUS_PENDING_PUSH);
 
        if (pendingMasters.isEmpty()) {
            log.info("No IRM records pending push to DGFT.");
            return;
        }
 
        log.info("Found {} IRM master record(s) pending push to DGFT.", pendingMasters.size());
 
        for (IrmMessageMaster master : pendingMasters) {
            try {
                pushSingleRecord(master);
            } catch (Exception ex) {
                log.error("Failed to push IRM master record with id {} to DGFT", master.getId(), ex);
                master.setDgftMsgPushError(ex.getMessage());
                master.setModifiedBy(JOB_USER);
                master.setModifiedDate(LocalDateTime.now());
                irmMessageMasterRepository.save(master);
            }
        }
    }
 
    private void pushSingleRecord(IrmMessageMaster master) {
 
        DgftIrmOutboundRequestDto requestDto = new DgftIrmOutboundRequestDto(null, null, null);
        requestDto.setUniqueTxId(master.getUniqueTxId());
        requestDto.setRequestJsonObj(master.getRequestJsonObj());
 
        DgftApiResponseDto response = mockDgftApiClient.pushIrmDetails(requestDto);
 
        LocalDateTime now = LocalDateTime.now();
 
        // ---- Update DGFT_IRM_MESSAGE_MASTER ----
        master.setDgftAckStatus(ACK_STATUS_VALIDATED);
        master.setStatus(STATUS_PUSH_SUCCESS);
        master.setResponseJsonObj(response.getResponseJson());
        master.setDgftMsgPushError(null);
        master.setDgftPushInitTime(now);
        master.setModifiedBy(JOB_USER);
        master.setModifiedDate(now);
        irmMessageMasterRepository.save(master);
 
        // ---- Update DGFT_IRM_MESSAGE_DETAIL (linked rows) ----
        List<IrmMessageDetail> details =
                irmMessageDetailRepository.findByDgftIrmMsgMasterId(master.getId());
 
        for (IrmMessageDetail detail : details) {
            detail.setDgftAckStatus(null);
            detail.setStatus(DETAIL_STATUS_PENDING);
            detail.setModifiedBy(JOB_USER);
            detail.setModifiedDate(now);
        }
        irmMessageDetailRepository.saveAll(details);
 
        log.info("Successfully pushed IRM master id [{}] to DGFT. {} detail record(s) updated to PENDING.",
                master.getId(), details.size());
    }
}