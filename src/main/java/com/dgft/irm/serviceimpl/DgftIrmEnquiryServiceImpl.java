package com.dgft.irm.serviceimpl;
 
import com.dgft.irm.constants.AppConstants;
import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.entity.IrmMaster;
import com.dgft.irm.entity.IrmMessageDetail;
import com.dgft.irm.entity.IrmMessageMaster;
import com.dgft.irm.mapper.IrmMasterHisMapper;
import com.dgft.irm.repository.IrmMasterHisRepository;
import com.dgft.irm.repository.IrmMasterRepository;
import com.dgft.irm.repository.IrmMessageDetailRepository;
import com.dgft.irm.repository.IrmMessageMasterRepository;
import com.dgft.irm.scheduler.DgftEnquiryApiClient;
import com.dgft.irm.service.DgftIrmEnquiryService;
import com.dgft.irm.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
/**
 * Step 4 of the DGFT new-records flow: "DGFT IRM Enquiry Service Scheduler".
 * Reviews the status of IRM batches that Step 3 pushed successfully
 * (dgft_irm_message_master.STATUS = MSG_PUSH_SUCCESS) and, once DGFT
 * confirms the batch via enquiry, updates the source-of-truth
 * dgft_irm_master row(s):
 *
 *   FLAG                 = P
 *   STATUS               = ACTIVE
 *   DGFT_FLAG             = F
 *   DGFT_STATUS           = Validated
 *   MASTER_DETAIL_STATUS = MSG_PUSH_SUCCESS
 *
 * Also writes a dgft_irm_master_his audit snapshot for each row updated,
 * mirroring the pattern Step 2 already uses.
 *
 * IRM rows whose MASTER_DETAIL_STATUS is already MSG_PUSH_SUCCESS are
 * treated as already confirmed and are skipped on subsequent runs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DgftIrmEnquiryServiceImpl implements DgftIrmEnquiryService {
 
    private final IrmMessageMasterRepository irmMessageMasterRepository;
    private final IrmMessageDetailRepository irmMessageDetailRepository;
    private final IrmMasterRepository irmMasterRepository;
    private final IrmMasterHisRepository irmMasterHisRepository;
    private final DgftEnquiryApiClient dgftEnquiryApiClient;
 
    @Value("${dgft.audit.system-user}")
    private String systemUser;
 
    @Override
    @Transactional
    public void enquireIrmStatus() {
 
        List<IrmMessageMaster> pushedMasters =
                irmMessageMasterRepository.findByStatus(AppConstants.MSG_PUSH_SUCCESS);
 
        if (pushedMasters.isEmpty()) {
            log.info("No pushed IRM batches found for DGFT enquiry.");
            return;
        }
 
        log.info("Found {} pushed IRM batch(es) eligible for DGFT enquiry check.", pushedMasters.size());
 
        for (IrmMessageMaster master : pushedMasters) {
            try {
                enquireSingleBatch(master);
            } catch (Exception ex) {
                log.error("DGFT enquiry failed for message master id {}", master.getId(), ex);
            }
        }
    }
 
    private void enquireSingleBatch(IrmMessageMaster master) {
 
        List<IrmMessageDetail> details =
                irmMessageDetailRepository.findByDgftIrmMsgMasterId(master.getId());
 
        // Only carry forward IRM master rows not already confirmed by a
        // previous enquiry run (guards against re-processing every tick).
        List<IrmMaster> pendingIrmMasters = new ArrayList<>();
 
        for (IrmMessageDetail detail : details) {
            irmMasterRepository.findByIrmNumber(detail.getIrmNumber()).ifPresent(irmMaster -> {
                if (!AppConstants.MSG_PUSH_SUCCESS.equals(irmMaster.getMasterDetailStatus())) {
                    pendingIrmMasters.add(irmMaster);
                }
            });
        }
 
        if (pendingIrmMasters.isEmpty()) {
            log.debug("Message master id {} already confirmed by a previous enquiry run - skipping.",
                    master.getId());
            return;
        }
 
        ApiResponseDto<String> response = dgftEnquiryApiClient.enquireStatus(master.getUniqueTxId());
 
        if (response == null || !response.isSuccess()) {
            log.error("DGFT enquiry did not return a validated status for uniqueTxId {}. Message: {}",
                    master.getUniqueTxId(),
                    response == null ? "no response" : response.getMessage());
            return;
        }
 
        LocalDateTime now = LocalDateTime.now();
 
        for (IrmMaster irmMaster : pendingIrmMasters) {
 
            irmMaster.setFlag(AppConstants.FLAG_PUSHED_TO_STAGING);
            irmMaster.setStatus(AppConstants.STATUS_ACTIVE);
            irmMaster.setDgftFlag(AppConstants.DGFT_FLAG_FRESH);
            irmMaster.setDgftStatus(AppConstants.DGFT_STATUS_VALIDATED);
            irmMaster.setMasterDetailStatus(AppConstants.MSG_PUSH_SUCCESS);
            irmMaster.setModifiedBy(systemUser);
            irmMaster.setModifiedDate(now);
 
            irmMasterRepository.save(irmMaster);
 
            irmMasterHisRepository.save(IrmMasterHisMapper.fromMaster(
                    irmMaster, AppConstants.TRIGGER_STATUS_ENQUIRY_VALIDATED, IdGenerator.next(), now));
        }
 
        log.info("DGFT enquiry confirmed for uniqueTxId {}. {} dgft_irm_master row(s) updated.",
                master.getUniqueTxId(), pendingIrmMasters.size());
    }
}