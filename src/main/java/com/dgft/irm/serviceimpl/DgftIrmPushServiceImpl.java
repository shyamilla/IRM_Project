package com.dgft.irm.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dgft.irm.dto.request.DgftIrmDto;
import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;
import com.dgft.irm.entity.IrmMaster;
import com.dgft.irm.entity.IrmMessageDetail;
import com.dgft.irm.entity.IrmMessageMaster;
import com.dgft.irm.entity.IrmMsgTxStatusLog;
import com.dgft.irm.repository.IrmMasterRepository;
import com.dgft.irm.repository.IrmMessageDetailRepository;
import com.dgft.irm.repository.IrmMessageMasterRepository;
import com.dgft.irm.repository.IrmMsgTxStatusLogRepository;
import com.dgft.irm.service.DgftIrmPushService;
import com.dgft.irm.service.DgftMessageGenerationService;
import com.dgft.irm.service.DgftMockApiService;
import com.dgft.irm.util.IdGenerator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DgftIrmPushServiceImpl implements DgftIrmPushService {

    private final DgftMessageGenerationService dgftMessageGenerationService;
    private final DgftMockApiService dgftMockApiService;

    private final IrmMasterRepository irmMasterRepository;
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
            log.info("No eligible IRM records found to push to DGFT.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        IrmMessageMaster messageMaster = new IrmMessageMaster();
        messageMaster.setId(IdGenerator.next());
        messageMaster.setUniqueTxId(request.getUniqueTxId());
        messageMaster.setStatus("JSON_PUSHED");
        messageMaster.setDgftAckStatus("AWAITING_DGFT_ACK");
        messageMaster.setDgftPushInitTime(now);
        messageMaster.setAddedBy(systemUser);
        messageMaster.setAddedDate(now);

        irmMessageMasterRepository.save(messageMaster);

        boolean success = dgftMockApiService.pushIrmToDgft(request);

        if (!success) {
            messageMaster.setStatus("FAILED");
            messageMaster.setDgftAckStatus("FAILED");
            messageMaster.setDgftMsgPushError("Failed to push IRM records to DGFT mock API");
            messageMaster.setModifiedBy(systemUser);
            messageMaster.setModifiedDate(LocalDateTime.now());

            irmMessageMasterRepository.save(messageMaster);

            saveStatusLog(
                    messageMaster.getId(),
                    createStatusSummaryJson(request, "FAILED")
            );

            log.error("IRM JSON push failed. TxId={}", request.getUniqueTxId());
            return;
        }

        for (DgftIrmDto irmDto : request.getIrmList()) {

            IrmMessageDetail detail = new IrmMessageDetail();

            detail.setId(IdGenerator.next());
            detail.setDgftIrmMsgMasterId(messageMaster.getId());
            detail.setIrmNumber(irmDto.getIrmNumber());

            if (irmDto.getIrmIssueDate() != null && !irmDto.getIrmIssueDate().isBlank()) {
                detail.setIrmIssueDate(
                        LocalDate.parse(irmDto.getIrmIssueDate(), DGFT_DATE_FORMAT)
                );
            }

            detail.setStatus(1); // 1 = JSON_PUSHED
            detail.setDgftAckStatus("AWAITING_DGFT_ACK");
            detail.setAddedBy(systemUser);
            detail.setAddedDate(now);

            irmMessageDetailRepository.save(detail);

            IrmMaster irmMaster = irmMasterRepository
                    .findByIrmNumber(irmDto.getIrmNumber())
                    .orElseThrow(() -> new RuntimeException(
                            "IRM not found: " + irmDto.getIrmNumber()
                    ));

            irmMaster.setDgftFlag("P");
            irmMaster.setDgftStatus("PUSH_REQUEST_SENT");
            irmMaster.setModifiedBy(systemUser);
            irmMaster.setModifiedDate(now);

            irmMasterRepository.save(irmMaster);
        }

        saveStatusLog(
                messageMaster.getId(),
                createStatusSummaryJson(request, "JSON_PUSHED")
        );

        log.info("IRM JSON successfully pushed and DB updated. TxId={}, Records={}",
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
}