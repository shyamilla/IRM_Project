
package com.dgft.irm.scheduler;
 
import com.dgft.irm.service.DgftIrmApiPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
 
/**
 * DGFT API IRM Push Service Scheduler (Step 3).
 * Executes periodically to push pending IRM details from the staging
 * tables to the DGFT end via DgftIrmApiPushService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DgftIrmApiPushScheduler {
 
    private final DgftIrmApiPushService dgftIrmApiPushService;
 
    @Value("${dgft.scheduler.irm-api-push.enabled}")
    private boolean enabled;
 
    @Scheduled(cron = "${dgft.scheduler.irm-api-push.cron}")
    public void executeIrmPushJob() {
 
        if (!enabled) {
            log.info("DGFT API IRM Push Scheduler is disabled.");
            return;
        }
 
        log.info("DGFT API IRM Push Scheduler triggered.");
        try {
            dgftIrmApiPushService.pushIrmDetailsToDgft();
            log.info("DGFT API IRM Push Scheduler execution completed successfully.");
        } catch (Exception ex) {
            log.error("DGFT API IRM Push Scheduler execution failed.", ex);
        }
    }
}
 