package com.dgft.irm.serviceimpl;
 
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
 
import com.dgft.irm.constants.AppConstants;
import com.dgft.irm.dto.request.DgftIrmDto;
import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;
import com.dgft.irm.entity.IrmMaster;
import com.dgft.irm.repository.IrmMasterRepository;
import com.dgft.irm.service.DgftMessageGenerationService;
 
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
@Service
@RequiredArgsConstructor
@Slf4j
public class DgftMessageGenerationServiceImpl implements DgftMessageGenerationService {
 
    private final IrmMasterRepository irmMasterRepository;
 
    @Value("${dgft.scheduler.irm-push.batch-size:20}")
    private int batchSize;
 
    // How long a partial (< batchSize) group of fresh records is
    // allowed to sit before being flushed as a short batch anyway.
    @Value("${dgft.scheduler.irm-push.max-wait-minutes:15}")
    private long maxWaitMinutes;
 
    private static final DateTimeFormatter DGFT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("ddMMyyyy");
 
    @Override
    @Transactional
    public DgftIrmOutboundRequestDto generateFreshIrmJson() {
 
        List<IrmMaster> candidates =
                irmMasterRepository.findByDgftFlagAndDgftStatusOrderByAddedDateAsc(
                        AppConstants.DGFT_FLAG_FRESH,
                        AppConstants.DGFT_STATUS_AWAITING_REQUEST_INITIATED,
                        PageRequest.of(0, batchSize)
                );
 
        if (candidates.isEmpty()) {
            return emptyRequest();
        }
 
        boolean fullBatch = candidates.size() >= batchSize;
 
        if (!fullBatch) {
            LocalDateTime oldestAddedDate = candidates.get(0).getAddedDate();
            boolean waitExpired = oldestAddedDate != null
                    && oldestAddedDate.isBefore(LocalDateTime.now().minusMinutes(maxWaitMinutes));
 
            if (!waitExpired) {
                log.info("Only {} fresh IRM record(s) eligible (< {}), oldest still within the "
                                + "{}-minute wait window - holding for now.",
                        candidates.size(), batchSize, maxWaitMinutes);
                return emptyRequest();
            }
 
            log.info("Flushing a short batch of {} record(s) - oldest has been waiting past "
                            + "the {}-minute limit.",
                    candidates.size(), maxWaitMinutes);
        }
 
        String uniqueTxId = generateUniqueTxId();
 
        List<DgftIrmDto> irmList = candidates.stream()
                .map(this::mapToDgftIrmDto)
                .toList();
 
        return new DgftIrmOutboundRequestDto(uniqueTxId, irmList, uniqueTxId);
    }
 
    private DgftIrmOutboundRequestDto emptyRequest() {
        return new DgftIrmOutboundRequestDto(null, Collections.emptyList(), null);
    }
 
    private DgftIrmDto mapToDgftIrmDto(IrmMaster irm) {
 
        return DgftIrmDto.builder()
                .bankRefNumber(irm.getBankReferenceNumber())
                .irmIssueDate(irm.getIrmIssueDate() != null
                        ? irm.getIrmIssueDate().format(DGFT_DATE_FORMAT)
                        : null)
                .irmNumber(irm.getIrmNumber())
                .irmStatus("F")
                .ifscCode(irm.getIfscCode())
                .remittanceAdCode(irm.getAdCode())
                .remittanceDate(irm.getRemittanceDate().format(DGFT_DATE_FORMAT))
                .remittanceFCC(irm.getCurrency())
                .remittanceFCAmount(irm.getRemittanceAmount())
                .inrCreditAmount(irm.getInrCreditAmount())
                .iecCode(irm.getIeCode() == null ? "" : irm.getIeCode())
                .panNumber(irm.getPanNumber())
                .remitterName(irm.getRemitterName())
                .remitterCountry(irm.getRemitterCountry())
                .purposeOfRemittance(irm.getPurposeOfRemittance())
                .bankAccountNo(irm.getBankAccountNumber())
                .build();
    }
 
    private String generateUniqueTxId() {
        return "TXN" + System.currentTimeMillis();
    }
}
