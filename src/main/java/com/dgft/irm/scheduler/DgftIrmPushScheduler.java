package com.dgft.irm.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dgft.irm.service.DgftIrmPushService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DgftIrmPushScheduler {

    private final DgftIrmPushService dgftIrmPushService;

    @Value("${dgft.scheduler.irm-push.enabled}")
    private boolean enabled;

    @Scheduled(cron = "${dgft.scheduler.irm-push.cron}")
    public void pushIrmRecords() {

        if (!enabled) {
            log.info("DGFT IRM Push Scheduler is disabled.");
            return;
        }

        log.info("DGFT IRM Push Scheduler started.");

        dgftIrmPushService.pushIrmRecordsToDgft();
    }
}
