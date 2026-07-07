package com.dgft.irm.job;

import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import com.dgft.irm.dto.response.IngestionSummaryResponseDto;
import com.dgft.irm.service.IrmIngestionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Quartz job - fires on the cron defined by `dgft.quartz.cron` in
 * application.yml. Delegates all real work to IrmIngestionService so the
 * same logic can also be triggered synchronously via the REST controller.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IrmCsvIngestionJob implements Job {
    private final IrmIngestionService irmIngestionService;

    @Override
    public void execute(JobExecutionContext context) {

        log.info("==================================================");
        log.info("DGFT Quartz Scheduler Triggered");
        log.info("Job Name : {}", context.getJobDetail().getKey());
        log.info("Fire Time : {}", context.getFireTime());
        log.info("==================================================");

        List<IngestionSummaryResponseDto> summaries = irmIngestionService.processInboundFolder();

        summaries.forEach(s -> log.info(
                "Processed File={} Total={} Pass={} Fail={} Ack={}",
                s.getFileName(),
                s.getTotalRecords(),
                s.getPassCount(),
                s.getFailCount(),
                s.getAckFilePath()));

        log.info("DGFT Quartz Scheduler Completed");
    }
}
