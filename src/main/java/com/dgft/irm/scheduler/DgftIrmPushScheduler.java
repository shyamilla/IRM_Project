package com.dgft.irm.scheduler;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
 
import com.dgft.irm.service.DgftIrmPushService;
 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
 
/**
 * DGFT IRM Push Service Scheduler (Step 2).
 * On each trigger, drains the full backlog of fresh DGFT_IRM_MASTER
 * records into the staging tables, one batch of
 * dgft.scheduler.irm-push.batch-size (default 20) records per DB
 * transaction, instead of one transaction per record (or one batch
 * per tick, leaving the rest of the backlog for later).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DgftIrmPushScheduler {
 
    private final DgftIrmPushService dgftIrmPushService;
 
    @Value("${dgft.scheduler.irm-push.enabled}")
    private boolean enabled;

    // Migrated to Quartz DgftIrmPushJob. Commenting out Spring's @Scheduled to prevent duplicate execution.
    // @Scheduled(cron = "${dgft.scheduler.irm-push.cron}")
    public void pushIrmRecords() {
 
        if (!enabled) {
            log.info("DGFT IRM Push Scheduler is disabled.");
            return;
        }
 
        log.info("DGFT IRM Push Scheduler started.");
 
        int batchCount = 0;
        boolean hasMore = true;
 
        while (hasMore) {
            hasMore = dgftIrmPushService.pushNextBatchToDgft();
            if (hasMore) {
                batchCount++;
            }
        }
 
        log.info("DGFT IRM Push Scheduler completed. Batches processed={}", batchCount);
    }
}