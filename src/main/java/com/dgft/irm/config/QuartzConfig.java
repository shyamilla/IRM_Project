package com.dgft.irm.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dgft.irm.job.IrmCsvIngestionJob;
import com.dgft.irm.job.DgftIrmPushJob;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class QuartzConfig {

    @Value("${dgft.quartz.cron}")
    private String cronExpression;

    @Value("${dgft.scheduler.irm-push.cron}")
    private String irmPushCronExpression;

    @Bean
    public JobDetail irmCsvIngestionJobDetail() {

        log.info("Creating Quartz JobDetail : irmCsvIngestionJob");

        return JobBuilder.newJob(IrmCsvIngestionJob.class)
                .withIdentity("irmCsvIngestionJob", "dgftGroup")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger irmCsvIngestionTrigger(JobDetail irmCsvIngestionJobDetail) {

        log.info("Creating Quartz Trigger with cron expression : {}", cronExpression);

        return TriggerBuilder.newTrigger()
                .forJob(irmCsvIngestionJobDetail)
                .withIdentity("irmCsvIngestionTrigger", "dgftGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }

    @Bean
    public JobDetail dgftIrmPushJobDetail() {

        log.info("Creating Quartz JobDetail : dgftIrmPushJob");

        return JobBuilder.newJob(DgftIrmPushJob.class)
                .withIdentity("dgftIrmPushJob", "dgftGroup")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger dgftIrmPushTrigger(JobDetail dgftIrmPushJobDetail) {

        log.info("Creating Quartz Trigger with cron expression : {}", irmPushCronExpression);

        return TriggerBuilder.newTrigger()
                .forJob(dgftIrmPushJobDetail)
                .withIdentity("dgftIrmPushTrigger", "dgftGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(irmPushCronExpression))
                .build();
    }
}