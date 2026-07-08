package com.dgft.irm.scheduler;
 
import com.dgft.irm.service.DgftIrmApiPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
 
/**
 * DGFT API IRM Push Service Scheduler.
 * Executes periodically to push pending IRM details from the staging
 * tables to the DGFT end via DgftIrmApiPushService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DgftIrmApiPushScheduler {
 
    private final DgftIrmApiPushService dgftIrmApiPushService;
 
    // Default: every 5 minutes. Override via application.properties:
    // dgft.irm.push.scheduler.cron=0 */5 * * * *
    @Scheduled(cron = "${dgft.irm.push.scheduler.cron:0 */5 * * * *}")
    public void executeIrmPushJob() {
        log.info("DGFT IRM Push Scheduler triggered.");
        try {
            dgftIrmApiPushService.pushIrmDetailsToDgft();
            log.info("DGFT IRM Push Scheduler execution completed successfully.");
        } catch (Exception ex) {
            log.error("DGFT IRM Push Scheduler execution failed.", ex);
        }
    }
}