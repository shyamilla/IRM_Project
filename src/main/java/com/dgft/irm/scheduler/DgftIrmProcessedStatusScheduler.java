
package com.dgft.irm.scheduler;
 
import com.dgft.irm.service.DgftIrmProcessedStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
 
/**
 * DGFT API IRM Processed Status Service Scheduler (Step 5).
 * Executes periodically to check whether IRM batches previously pushed
 * via API have been reviewed by DGFT, and to reflect the outcome
 * (still pending / fully processed / failed) on the staging tables.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DgftIrmProcessedStatusScheduler {
 
    private final DgftIrmProcessedStatusService dgftIrmProcessedStatusService;
 
    @Value("${dgft.scheduler.irm-processed-status.enabled}")
    private boolean enabled;
 
    @Scheduled(cron = "${dgft.scheduler.irm-processed-status.cron}")
    public void executeIrmProcessedStatusJob() {
 
        if (!enabled) {
            log.info("DGFT API IRM Processed Status Scheduler is disabled.");
            return;
        }
 
        log.info("DGFT API IRM Processed Status Scheduler triggered.");
        try {
            dgftIrmProcessedStatusService.checkProcessedStatus();
            log.info("DGFT API IRM Processed Status Scheduler execution completed successfully.");
        } catch (Exception ex) {
            log.error("DGFT API IRM Processed Status Scheduler execution failed.", ex);
        }
    }
}