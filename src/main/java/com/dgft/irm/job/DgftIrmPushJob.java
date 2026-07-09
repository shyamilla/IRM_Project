package com.dgft.irm.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.dgft.irm.scheduler.DgftIrmPushScheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Quartz job - fires on the cron defined by `dgft.scheduler.irm-push.cron` in
 * application.properties. Delegates all real work to DgftIrmPushScheduler.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DgftIrmPushJob implements Job {

    private final DgftIrmPushScheduler dgftIrmPushScheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        log.info("==================================================");
        log.info("DGFT Quartz IRM Push Job Triggered");
        log.info("Job Name : {}", context.getJobDetail().getKey());
        log.info("Fire Time : {}", context.getFireTime());
        log.info("==================================================");

        try {
            dgftIrmPushScheduler.pushIrmRecords();
            log.info("DGFT Quartz IRM Push Job execution completed successfully.");
        } catch (Exception ex) {
            log.error("DGFT Quartz IRM Push Job execution failed.", ex);
            throw new JobExecutionException(ex);
        }
    }
}
