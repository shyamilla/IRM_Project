 
package com.dgft.irm.serviceimpl;
 
import com.dgft.irm.constants.AppConstants;
import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.RecordResultDto;
import com.dgft.irm.entity.IrmMessageDetail;
import com.dgft.irm.entity.IrmMessageMaster;
import com.dgft.irm.entity.IrmMsgTxStatusLog;
import com.dgft.irm.repository.IrmMessageDetailRepository;
import com.dgft.irm.repository.IrmMessageMasterRepository;
import com.dgft.irm.repository.IrmMsgTxStatusLogRepository;
import com.dgft.irm.scheduler.DgftIrmProcessedStatusApiClient;
import com.dgft.irm.service.DgftIrmProcessedStatusService;
import com.dgft.irm.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
 
/**
 * Step 5 of the DGFT new-records flow: "DGFT API IRM Processed Status
 * Service Scheduler".
 *
 * Picks up batches previously pushed successfully (Step 3, STATUS =
 * MSG_PUSH_SUCCESS) or already checked once and still awaiting DGFT review
 * (STATUS = MSG_PUSH_PENDING_PROCESS), calls DGFT's "get response status"
 * API, and updates the staging tables only:
 *
 *   Still pending review:
 *     MASTER : DGFT_ACK_STATUS = Validated, STATUS = MSG_PUSH_PENDING_PROCESS
 *     DETAIL : STATUS = PENDING,            DGFT_ACK_STATUS = Validated
 *
 *   Reviewed, all records succeeded:
 *     MASTER : DGFT_ACK_STATUS = Processed, STATUS = MSG_PUSH_FULLY_PROCESSED
 *     DETAIL : STATUS = PROCESSED,          DGFT_ACK_STATUS = Processed
 *
 *   Reviewed, all records failed:
 *     MASTER : DGFT_ACK_STATUS = FAILED,    STATUS = MSG_PUSH_PROCESS_FAILED
 *     DETAIL : STATUS = FAILED,             DGFT_ACK_STATUS = Errored
 *
 *   Reviewed, some records failed (partial):
 *     MASTER : DGFT_ACK_STATUS = Processed, STATUS = MSG_PUSH_FULLY_PROCESSED
 *     DETAIL (failed ones)   : STATUS = FAILED,    DGFT_ACK_STATUS = Errored
 *     DETAIL (succeeded ones): STATUS = PROCESSED, DGFT_ACK_STATUS = Processed
 *
 * MSG_PUSH_FULLY_PROCESSED / MSG_PUSH_PROCESS_FAILED are terminal - such
 * masters are no longer picked up by subsequent runs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DgftIrmProcessedStatusServiceImpl implements DgftIrmProcessedStatusService {
 
    private final IrmMessageMasterRepository irmMessageMasterRepository;
    private final IrmMessageDetailRepository irmMessageDetailRepository;
    private final IrmMsgTxStatusLogRepository irmMsgTxStatusLogRepository;
    private final DgftIrmProcessedStatusApiClient dgftIrmProcessedStatusApiClient;
 
    @Value("${dgft.audit.system-user}")
    private String systemUser;
 
    @Override
    @Transactional
    public void checkProcessedStatus() {
 
        List<IrmMessageMaster> candidates = irmMessageMasterRepository.findByStatusIn(
                List.of(AppConstants.MSG_PUSH_SUCCESS, AppConstants.MSG_PUSH_PENDING_PROCESS));
 
        if (candidates.isEmpty()) {
            log.info("No IRM batches awaiting DGFT processed-status check.");
            return;
        }
 
        log.info("Found {} IRM batch(es) eligible for DGFT processed-status check.", candidates.size());
 
        for (IrmMessageMaster master : candidates) {
            try {
                checkSingleBatch(master);
            } catch (Exception ex) {
                log.error("DGFT processed-status check failed for message master id {}", master.getId(), ex);
            }
        }
    }
 
    private void checkSingleBatch(IrmMessageMaster master) {
 
        List<IrmMessageDetail> details =
                irmMessageDetailRepository.findByDgftIrmMsgMasterId(master.getId());
 
        if (details.isEmpty()) {
            log.warn("Message master id {} has no detail rows - skipping.", master.getId());
            return;
        }
 
        List<String> irmRefNumbers = details.stream()
                .map(IrmMessageDetail::getIrmNumber)
                .collect(Collectors.toList());
 
        ApiResponseDto<List<RecordResultDto>> response =
                dgftIrmProcessedStatusApiClient.getProcessedStatus(master.getUniqueTxId(), irmRefNumbers);
 
        if (response == null || !response.isSuccess()) {
            log.error("DGFT processed-status call failed for uniqueTxId {}. Message: {}",
                    master.getUniqueTxId(), response == null ? "no response" : response.getMessage());
            return;
        }
 
        List<RecordResultDto> results = response.getData();
        LocalDateTime now = LocalDateTime.now();
 
        if (results == null || results.isEmpty()) {
            markStillPending(master, details, now);
            return;
        }
 
        applyReviewedOutcome(master, details, results, now);
    }
 
    private void markStillPending(IrmMessageMaster master, List<IrmMessageDetail> details, LocalDateTime now) {
 
        master.setDgftAckStatus(AppConstants.DGFT_STATUS_VALIDATED);
        master.setStatus(AppConstants.MSG_PUSH_PENDING_PROCESS);
        master.setDgftLastTxStatusInitTime(now);
        master.setModifiedBy(systemUser);
        master.setModifiedDate(now);
        irmMessageMasterRepository.save(master);
 
        for (IrmMessageDetail detail : details) {
            detail.setStatus(AppConstants.MSG_DETAIL_STATUS_PENDING);
            detail.setDgftAckStatus(AppConstants.DGFT_STATUS_VALIDATED);
            detail.setModifiedBy(systemUser);
            detail.setModifiedDate(now);
            irmMessageDetailRepository.save(detail);
        }
 
        saveStatusLog(master.getId(),
                buildStatusJson(master.getUniqueTxId(), AppConstants.MSG_PUSH_PENDING_PROCESS, details.size()));
 
        log.info("DGFT has not yet reviewed uniqueTxId {}. Marked as {}.",
                master.getUniqueTxId(), AppConstants.MSG_PUSH_PENDING_PROCESS);
    }
 
    private void applyReviewedOutcome(IrmMessageMaster master, List<IrmMessageDetail> details,
                                       List<RecordResultDto> results, LocalDateTime now) {
 
        Map<String, RecordResultDto> resultsByIrmNumber = results.stream()
                .collect(Collectors.toMap(RecordResultDto::getIrmRefNumber, Function.identity(), (a, b) -> a));
 
        int total = details.size();
        int failedCount = 0;
 
        for (IrmMessageDetail detail : details) {
 
            RecordResultDto result = resultsByIrmNumber.get(detail.getIrmNumber());
 
            if (result == null) {
                // DGFT didn't report on this record yet within this response -
                // leave it pending, don't force an outcome onto it.
                continue;
            }
 
            if (result.isSuccess()) {
                detail.setStatus(AppConstants.MSG_DETAIL_STATUS_PROCESSED);
                detail.setDgftAckStatus(AppConstants.DGFT_STATUS_PROCESSED);
            } else {
                failedCount++;
                detail.setStatus(AppConstants.MSG_DETAIL_STATUS_FAILED);
                detail.setDgftAckStatus(AppConstants.MSG_DETAIL_ACK_STATUS_ERRORED);
                detail.setDgftErrorDetails(result.getReasons() == null
                        ? null
                        : String.join("; ", result.getReasons()));
            }
 
            detail.setModifiedBy(systemUser);
            detail.setModifiedDate(now);
            irmMessageDetailRepository.save(detail);
        }
 
        boolean allFailed = failedCount == total;
 
        if (allFailed) {
            master.setDgftAckStatus(AppConstants.MSG_MASTER_ACK_STATUS_FAILED_ALL);
            master.setStatus(AppConstants.MSG_PUSH_PROCESS_FAILED);
        } else {
            master.setDgftAckStatus(AppConstants.DGFT_STATUS_PROCESSED);
            master.setStatus(AppConstants.MSG_PUSH_FULLY_PROCESSED);
        }
 
        master.setDgftLastTxStatusInitTime(now);
        master.setModifiedBy(systemUser);
        master.setModifiedDate(now);
        irmMessageMasterRepository.save(master);
 
        saveStatusLog(master.getId(), buildStatusJson(master.getUniqueTxId(), master.getStatus(), total));
 
        log.info("DGFT reviewed uniqueTxId {}: {}/{} failed. Master marked as {}.",
                master.getUniqueTxId(), failedCount, total, master.getStatus());
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