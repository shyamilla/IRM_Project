package com.dgft.irm.scheduler;
 
import com.dgft.irm.service.DgftIrmEnquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
 
/**
 * DGFT IRM Enquiry Service Scheduler (Step 4).
 * Executes periodically to review the DGFT status of IRM batches
 * already pushed to the staging table and reflect confirmed progress
 * onto dgft_irm_master.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DgftIrmEnquiryScheduler {
 
    private final DgftIrmEnquiryService dgftIrmEnquiryService;
 
    @Value("${dgft.scheduler.irm-enquiry.enabled}")
    private boolean enabled;
 
    @Scheduled(cron = "${dgft.scheduler.irm-enquiry.cron}")
    public void executeIrmEnquiryJob() {
 
        if (!enabled) {
            log.info("DGFT IRM Enquiry Scheduler is disabled.");
            return;
        }
 
        log.info("DGFT IRM Enquiry Scheduler triggered.");
        try {
            dgftIrmEnquiryService.enquireIrmStatus();
            log.info("DGFT IRM Enquiry Scheduler execution completed successfully.");
        } catch (Exception ex) {
            log.error("DGFT IRM Enquiry Scheduler execution failed.", ex);
        }
    }
}