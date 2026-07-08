package com.dgft.irm.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dgft.irm.constants.AppConstants;
import com.dgft.irm.dto.request.DgftIrmDto;
import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;
import com.dgft.irm.entity.IrmMaster;
import com.dgft.irm.entity.IrmMessageDetail;
import com.dgft.irm.entity.IrmMessageMaster;
import com.dgft.irm.entity.IrmMsgTxStatusLog;
import com.dgft.irm.mapper.IrmMasterHisMapper;
import com.dgft.irm.repository.IrmMasterHisRepository;
import com.dgft.irm.repository.IrmMasterRepository;
import com.dgft.irm.repository.IrmMessageDetailRepository;
import com.dgft.irm.repository.IrmMessageMasterRepository;
import com.dgft.irm.repository.IrmMsgTxStatusLogRepository;
import com.dgft.irm.service.DgftIrmPushService;
import com.dgft.irm.service.DgftMessageGenerationService;
import com.dgft.irm.service.DgftMockApiService;
import com.dgft.irm.util.IdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 2 of the DGFT new-records flow: "DGFT IRM Push Service Scheduler".
 * Pushes fresh IRM_MASTER rows (DGFT_FLAG='F', DGFT_STATUS='Awaiting request
 * initiated') into the intermediate staging tables (DGFT_IRM_MESSAGE_MASTER /
 * DGFT_IRM_MESSAGE_DETAIL) and updates DGFT_IRM_MASTER per the flow doc:
 *   FLAG            : P
 *   STATUS          : ACTIVE (unchanged)
 *   DGFT_FLAG       : F (unchanged)
 *   DGFT_STATUS     : Request initiated
 *   BANK_UNIQUE_TRANSACTION_ID : generated (== message master's unique tx id)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DgftIrmPushServiceImpl implements DgftIrmPushService {

    private final DgftMessageGenerationService dgftMessageGenerationService;
    private final DgftMockApiService dgftMockApiService;
    private final ObjectMapper objectMapper;

    private final IrmMasterRepository irmMasterRepository;
    private final IrmMasterHisRepository irmMasterHisRepository;
    private final IrmMessageMasterRepository irmMessageMasterRepository;
    private final IrmMessageDetailRepository irmMessageDetailRepository;
    private final IrmMsgTxStatusLogRepository irmMsgTxStatusLogRepository;

    @Value("${dgft.audit.system-user}")
    private String systemUser;

    private static final DateTimeFormatter DGFT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("ddMMyyyy");

    @Override
    @Transactional
    public void pushIrmRecordsToDgft() {

        DgftIrmOutboundRequestDto request =
                dgftMessageGenerationService.generateFreshIrmJson();

        if (request.getIrmList() == null || request.getIrmList().isEmpty()) {
            log.info("No eligible IRM records found to push to staging tables.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // ---- DGFT_IRM_MESSAGE_MASTER: basic details - unique tx id + request json ----
        IrmMessageMaster messageMaster = new IrmMessageMaster();
        messageMaster.setId(IdGenerator.next());
        messageMaster.setUniqueTxId(request.getUniqueTxId());
        messageMaster.setRequestJsonObj(toJson(request));
        messageMaster.setStatus(AppConstants.MSG_MASTER_STATUS_NEW);
        messageMaster.setDgftAckStatus(AppConstants.MSG_MASTER_ACK_STATUS_REQUEST_INITIATED);
        messageMaster.setDgftPushInitTime(now);
        messageMaster.setAddedBy(systemUser);
        messageMaster.setAddedDate(now);

        irmMessageMasterRepository.save(messageMaster);

        // Local persist to staging - not the real DGFT API call (that's Step 3).
        // Kept as a defensive guard around the mock hand-off only.
        boolean success = dgftMockApiService.pushIrmToDgft(request);

        if (!success) {
            messageMaster.setStatus("FAILED");
            messageMaster.setDgftAckStatus("FAILED");
            messageMaster.setDgftMsgPushError("Failed to stage IRM records for DGFT push");
            messageMaster.setModifiedBy(systemUser);
            messageMaster.setModifiedDate(LocalDateTime.now());

            irmMessageMasterRepository.save(messageMaster);

            saveStatusLog(
                    messageMaster.getId(),
                    createStatusSummaryJson(request, "FAILED")
            );

            log.error("IRM staging push failed. TxId={}", request.getUniqueTxId());
            return;
        }

        for (DgftIrmDto irmDto : request.getIrmList()) {

            // ---- DGFT_IRM_MESSAGE_DETAIL: status=NEW, ack status=null ----
            IrmMessageDetail detail = new IrmMessageDetail();

            detail.setId(IdGenerator.next());
            detail.setDgftIrmMsgMasterId(messageMaster.getId());
            detail.setIrmNumber(irmDto.getIrmNumber());

            if (irmDto.getIrmIssueDate() != null && !irmDto.getIrmIssueDate().isBlank()) {
                detail.setIrmIssueDate(
                        LocalDate.parse(irmDto.getIrmIssueDate(), DGFT_DATE_FORMAT)
                );
            }

            detail.setStatus(AppConstants.MSG_DETAIL_STATUS_NEW);
            detail.setDgftAckStatus(null);
            detail.setAddedBy(systemUser);
            detail.setAddedDate(now);

            irmMessageDetailRepository.save(detail);

            // ---- DGFT_IRM_MASTER: FLAG=P, DGFT_FLAG stays F, DGFT_STATUS=Request initiated ----
            IrmMaster irmMaster = irmMasterRepository
                    .findByIrmNumber(irmDto.getIrmNumber())
                    .orElseThrow(() -> new RuntimeException(
                            "IRM not found: " + irmDto.getIrmNumber()
                    ));

            irmMaster.setFlag(AppConstants.FLAG_PUSHED_TO_STAGING);
            irmMaster.setDgftStatus(AppConstants.DGFT_STATUS_REQUEST_INITIATED);
            irmMaster.setBankUniqueTransactionId(request.getUniqueTxId());
            irmMaster.setModifiedBy(systemUser);
            irmMaster.setModifiedDate(now);
            // DGFT_FLAG and STATUS intentionally untouched - flow doc keeps them F / ACTIVE here

            irmMasterRepository.save(irmMaster);

            // audit trail entry for this state transition
            irmMasterHisRepository.save(IrmMasterHisMapper.fromMaster(
                    irmMaster, AppConstants.TRIGGER_STATUS_PUSHED_TO_STAGING, IdGenerator.next(), now));
        }

        saveStatusLog(
                messageMaster.getId(),
                createStatusSummaryJson(request, AppConstants.MSG_MASTER_STATUS_NEW)
        );

        log.info("IRM records staged for DGFT push. TxId={}, Records={}",
                request.getUniqueTxId(),
                request.getIrmList().size());
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

    private String createStatusSummaryJson(
            DgftIrmOutboundRequestDto request,
            String transactionStatus) {

        return """
                {
                  "uniqueTxId":"%s",
                  "transactionStatus":"%s",
                  "recordCount":%d
                }
                """.formatted(
                request.getUniqueTxId(),
                transactionStatus,
                request.getIrmList().size());
    }

    private String toJson(DgftIrmOutboundRequestDto request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbound IRM request to JSON, TxId={}", request.getUniqueTxId(), e);
            return null;
        }
    }
}
