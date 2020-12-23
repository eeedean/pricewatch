package me.redoak.edean.pricewatch.logic.update;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Configuration for establishing {@link JobDetail} and {@link Trigger} for launching the {@link ProductUpdateJob}.
 */
@Slf4j
@Configuration
public class UpdateJobConfiguration {

    @Value("${me.redoak.edean.pricewatch.update.schedule:0 0 * 1/1 * ? *}")
    private String cronSchedule;

    /**
     * {@link PostConstruct} method for logging some configuration.
     */
    @PostConstruct
    public void postConstruct() {
        log.info("Configured update job with schedule: {}", cronSchedule);
    }

    /**
     * @return Durable {@link JobDetail} for the {@link ProductUpdateJob}.
     */
    @Bean
    public JobDetail updateJobDetail() {
        return JobBuilder
                .newJob()
                .withIdentity("updateAllProductsJobDetail")
                .storeDurably(true)
                .requestRecovery(false)
                .ofType(ProductUpdateJob.class)
                .build();
    }

    /**
     * @param updateJobDetail The created {@link JobDetail} for the {@link ProductUpdateJob}.
     *
     * @return A {@link Trigger} running the job every minute.
     */
    @Bean
    public Trigger updateJobTrigger(JobDetail updateJobDetail) {
        return TriggerBuilder
                .newTrigger()
                .withIdentity("updateAllProductsTrigger")
                .forJob(updateJobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule))
                .build();
    }
}
