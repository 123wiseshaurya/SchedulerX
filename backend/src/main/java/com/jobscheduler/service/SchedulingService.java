package com.jobscheduler.service;

import com.jobscheduler.entity.Job;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
public class SchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public void scheduleJob(Job job) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(JobExecutionJob.class)
                    .withIdentity("job-" + job.getId(), "job-group")
                    .usingJobData("jobId", job.getId())
                    .usingJobData("jobType", job.getJobType().name())
                    .build();

            Trigger trigger = createTrigger(job);

            scheduler.scheduleJob(jobDetail, trigger);
            logger.info("Job {} scheduled successfully", job.getId());

        } catch (SchedulerException e) {
            logger.error("Error scheduling job: {}", job.getId(), e);
            throw new RuntimeException("Failed to schedule job", e);
        }
    }

    public void cancelJob(Long jobId) {
        try {
            JobKey jobKey = new JobKey("job-" + jobId, "job-group");
            scheduler.deleteJob(jobKey);
            logger.info("Job {} cancelled successfully", jobId);
        } catch (SchedulerException e) {
            logger.error("Error cancelling job: {}", jobId, e);
        }
    }

    private Trigger createTrigger(Job job) {
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity("trigger-" + job.getId(), "trigger-group")
                .startAt(Date.from(job.getScheduledTime().atZone(ZoneId.of(job.getTimezone())).toInstant()));

        // Add repeat schedule if needed
        if (job.getRepeatPattern() != Job.RepeatPattern.ONCE) {
            SimpleScheduleBuilder scheduleBuilder = createScheduleBuilder(job.getRepeatPattern());
            triggerBuilder.withSchedule(scheduleBuilder);
        }

        return triggerBuilder.build();
    }

    private SimpleScheduleBuilder createScheduleBuilder(Job.RepeatPattern repeatPattern) {
        switch (repeatPattern) {
            case DAILY:
                return SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(24)
                        .repeatForever();
            case WEEKLY:
                return SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(24 * 7)
                        .repeatForever();
            case MONTHLY:
                return SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(24 * 30) // Approximate
                        .repeatForever();
            case YEARLY:
                return SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(24 * 365) // Approximate
                        .repeatForever();
            default:
                return SimpleScheduleBuilder.simpleSchedule();
        }
    }

    // Quartz Job class
    public static class JobExecutionJob implements org.quartz.Job {

        @Autowired
        private KafkaProducerService kafkaProducerService;

        @Autowired
        private JobService jobService;

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            Long jobId = dataMap.getLong("jobId");
            String jobType = dataMap.getString("jobType");

            logger.info("Executing scheduled job: {} of type: {}", jobId, jobType);

            try {
                // Trigger job execution via Kafka
                jobService.executeJobNow(jobId);
            } catch (Exception e) {
                logger.error("Error executing scheduled job: {}", jobId, e);
                throw new JobExecutionException("Failed to execute job", e);
            }
        }
    }
}