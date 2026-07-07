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

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class QuartzConfig {

    @Value("${dgft.quartz.cron}")
    private String cronExpression;

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
}