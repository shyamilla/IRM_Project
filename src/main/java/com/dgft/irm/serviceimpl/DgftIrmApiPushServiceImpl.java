package com.dgft.irm.serviceimpl;
 
import com.dgft.irm.constants.AppConstants;
import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.entity.IrmMessageDetail;
import com.dgft.irm.entity.IrmMessageMaster;
import com.dgft.irm.entity.IrmMsgTxStatusLog;
import com.dgft.irm.repository.IrmMasterRepository;
import com.dgft.irm.repository.IrmMessageDetailRepository;
import com.dgft.irm.repository.IrmMessageMasterRepository;
import com.dgft.irm.repository.IrmMsgTxStatusLogRepository;
import com.dgft.irm.scheduler.DgftApiClient;
import com.dgft.irm.service.DgftIrmApiPushService;
import com.dgft.irm.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
 
/**
 * Step 3 of the DGFT new-records flow: "DGFT API IRM Push Service Scheduler".
 * Pushes DGFT_IRM_MESSAGE_MASTER rows that Step 2 staged (STATUS =
 * MSG_PUSH_NEW) to the DGFT end via DgftApiClient, then updates:
 *
 *   DGFT_IRM_MESSAGE_MASTER : DGFT_ACK_STATUS = Validated
 *                             STATUS          = MSG_PUSH_SUCCESS
 *   DGFT_IRM_MESSAGE_DETAIL : DGFT_ACK_STATUS = null
 *                             STATUS          = PENDING
 *   DGFT_IRM_MASTER         : DGFT_STATUS     = Validated  (source-of-truth row)
 *   DGFT_IRM_MSG_TX_STATUS_LOG : new audit entry for this transaction
 *
 * On failure:
 *   DGFT_IRM_MESSAGE_MASTER : DGFT_ACK_STATUS = Failed
 *                             STATUS          = MSG_PUSH_PROCESS_FAILED
 *                             DGFT_MSG_PUSH_ERROR = <error message>
 *   (detail rows and the source IRM master row are left untouched so a
 *    reset-and-retry can pick the same batch back up cleanly)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DgftIrmApiPushServiceImpl implements DgftIrmApiPushService {
 
    private final IrmMessageMasterRepository irmMessageMasterRepository;
    private final IrmMessageDetailRepository irmMessageDetailRepository;
    private final IrmMasterRepository irmMasterRepository;
    private final IrmMsgTxStatusLogRepository irmMsgTxStatusLogRepository;
    private final DgftApiClient dgftApiClient;
 
    @Value("${dgft.audit.system-user}")
    private String systemUser;
 
    @Override
    @Transactional
    public void pushIrmDetailsToDgft() {
 
        List<IrmMessageMaster> pendingMasters =
                irmMessageMasterRepository.findByStatus(AppConstants.MSG_MASTER_STATUS_NEW);
 
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
                handlePushFailure(master, ex.getMessage());
            }
        }
    }
 
    private void pushSingleRecord(IrmMessageMaster master) {
 
        ApiResponseDto<String> response = dgftApiClient.pushIrm(master.getRequestJsonObj());
 
        if (response == null || !response.isSuccess()) {
            handlePushFailure(master, response == null ? "No response from DGFT" : response.getMessage());
            return;
        }
 
        LocalDateTime now = LocalDateTime.now();
 
        // ---- DGFT_IRM_MESSAGE_MASTER ----
        master.setDgftAckStatus(AppConstants.DGFT_STATUS_VALIDATED);
        master.setStatus(AppConstants.MSG_PUSH_SUCCESS);
        master.setResponseJsonObj(response.getData());
        master.setDgftMsgPushError(null);
        master.setDgftPushInitTime(now);
        master.setModifiedBy(systemUser);
        master.setModifiedDate(now);
        irmMessageMasterRepository.save(master);
 
        // ---- DGFT_IRM_MESSAGE_DETAIL (linked rows) ----
        List<IrmMessageDetail> details =
                irmMessageDetailRepository.findByDgftIrmMsgMasterId(master.getId());
 
        for (IrmMessageDetail detail : details) {
            detail.setDgftAckStatus(null);
            detail.setStatus(AppConstants.MSG_DETAIL_STATUS_PENDING);
            detail.setModifiedBy(systemUser);
            detail.setModifiedDate(now);
            irmMessageDetailRepository.save(detail);
 
            // ---- Reflect push success on the source-of-truth DGFT_IRM_MASTER row ----
            irmMasterRepository.findByIrmNumber(detail.getIrmNumber()).ifPresent(irmMaster -> {
                irmMaster.setDgftStatus(AppConstants.DGFT_STATUS_VALIDATED);
                irmMaster.setModifiedBy(systemUser);
                irmMaster.setModifiedDate(now);
                irmMasterRepository.save(irmMaster);
            });
        }
 
        saveStatusLog(master.getId(),
                buildStatusJson(master.getUniqueTxId(), AppConstants.MSG_PUSH_SUCCESS, details.size()));
 
        log.info("Successfully pushed IRM master id [{}] to DGFT. {} detail record(s) updated to PENDING.",
                master.getId(), details.size());
    }
 
    private void handlePushFailure(IrmMessageMaster master, String errorMessage) {
 
        LocalDateTime now = LocalDateTime.now();
 
        master.setStatus(AppConstants.MSG_PUSH_PROCESS_FAILED);
        master.setDgftAckStatus(AppConstants.DGFT_STATUS_FAILED);
        master.setDgftMsgPushError(errorMessage);
        master.setModifiedBy(systemUser);
        master.setModifiedDate(now);
        irmMessageMasterRepository.save(master);
 
        saveStatusLog(master.getId(),
                buildStatusJson(master.getUniqueTxId(), AppConstants.MSG_PUSH_PROCESS_FAILED, 0));
 
        log.error("Marked IRM master id [{}] as {}. Reason: {}",
                master.getId(), AppConstants.MSG_PUSH_PROCESS_FAILED, errorMessage);
    }
 
    private void saveStatusLog(String messageMasterId, String json) {
 
        IrmMsgTxStatusLog statusLog = new IrmMsgTxStatusLog();
        statusLog.setId(IdGenerator.next());
        statusLog.setDgftIrmMsgMasterId(messageMasterId);
        statusLog.setDgftTxStatusJsonObj(json);
        statusLog.setAddedBy(systemUser);
        statusLog.setAddedDate(LocalDateTime.now());
 
        irmMsgTxStatusLogRepository.save(statusLog);
    }
 
    private String buildStatusJson(String uniqueTxId, String transactionStatus, int recordCount) {
        return """
                {
                  "uniqueTxId":"%s",
                  "transactionStatus":"%s",
                  "recordCount":%d
                }
                """.formatted(uniqueTxId, transactionStatus, recordCount);
    }
}
 