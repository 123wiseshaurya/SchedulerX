package com.jobscheduler.service;

import com.jobscheduler.dto.BinaryJobRequest;
import com.jobscheduler.dto.EmailJobRequest;
import com.jobscheduler.dto.JobResponse;
import com.jobscheduler.entity.BinaryJob;
import com.jobscheduler.entity.EmailJob;
import com.jobscheduler.entity.Job;
import com.jobscheduler.entity.JobExecution;
import com.jobscheduler.repository.BinaryJobRepository;
import com.jobscheduler.repository.EmailJobRepository;
import com.jobscheduler.repository.JobExecutionRepository;
import com.jobscheduler.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private BinaryJobRepository binaryJobRepository;

    @Autowired
    private EmailJobRepository emailJobRepository;

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private SchedulingService schedulingService;

    // Create Binary Job
    public JobResponse createBinaryJob(BinaryJobRequest request) {
        logger.info("Creating binary job: {}", request.getName());

        BinaryJob job = new BinaryJob();
        job.setName(request.getName());
        job.setScheduledTime(request.getScheduledTime());
        job.setRepeatPattern(request.getRepeatPattern());
        job.setTimezone(request.getTimezone());
        job.setDelayMinutes(request.getDelayMinutes());
        job.setFilePath(request.getFilePath());
        job.setFileSize(request.getFileSize());
        job.setPresignedUrl(request.getPresignedUrl());
        job.setOriginalFilename(request.getOriginalFilename());
        job.setContentType(request.getContentType());

        // Calculate next run time for recurring jobs
        if (job.getRepeatPattern() != Job.RepeatPattern.ONCE) {
            job.setNextRun(calculateNextRun(job.getScheduledTime(), job.getRepeatPattern()));
        }

        BinaryJob savedJob = binaryJobRepository.save(job);
        
        // Schedule the job
        schedulingService.scheduleJob(savedJob);
        
        logger.info("Binary job created successfully with ID: {}", savedJob.getId());
        return new JobResponse(savedJob);
    }

    // Create Email Job
    public JobResponse createEmailJob(EmailJobRequest request) {
        logger.info("Creating email job: {}", request.getName());

        EmailJob job = new EmailJob();
        job.setName(request.getName());
        job.setScheduledTime(request.getScheduledTime());
        job.setRepeatPattern(request.getRepeatPattern());
        job.setTimezone(request.getTimezone());
        job.setDelayMinutes(request.getDelayMinutes());
        job.setRecipients(request.getRecipients());
        job.setSubject(request.getSubject());
        job.setContent(request.getContent());
        job.setTemplate(request.getTemplate());
        job.setAttachments(request.getAttachments());
        job.setHtmlContent(request.getHtmlContent());
        job.setSenderEmail(request.getSenderEmail());
        job.setSenderName(request.getSenderName());

        // Calculate next run time for recurring jobs
        if (job.getRepeatPattern() != Job.RepeatPattern.ONCE) {
            job.setNextRun(calculateNextRun(job.getScheduledTime(), job.getRepeatPattern()));
        }

        EmailJob savedJob = emailJobRepository.save(job);
        
        // Schedule the job
        schedulingService.scheduleJob(savedJob);
        
        logger.info("Email job created successfully with ID: {}", savedJob.getId());
        return new JobResponse(savedJob);
    }

    // Get all jobs
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(JobResponse::new)
                .collect(Collectors.toList());
    }

    // Get jobs with pagination
    public Page<JobResponse> getJobs(Pageable pageable) {
        return jobRepository.findAll(pageable)
                .map(JobResponse::new);
    }

    // Get job by ID
    public Optional<JobResponse> getJobById(Long id) {
        return jobRepository.findById(id)
                .map(JobResponse::new);
    }

    // Get jobs by status
    public List<JobResponse> getJobsByStatus(Job.JobStatus status) {
        return jobRepository.findByStatus(status).stream()
                .map(JobResponse::new)
                .collect(Collectors.toList());
    }

    // Update job status
    public JobResponse updateJobStatus(Long id, Job.JobStatus status) {
        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isPresent()) {
            Job job = jobOpt.get();
            job.setStatus(status);
            
            if (status == Job.JobStatus.RUNNING) {
                job.setLastRun(LocalDateTime.now());
            }
            
            Job savedJob = jobRepository.save(job);
            logger.info("Job {} status updated to {}", id, status);
            return new JobResponse(savedJob);
        }
        throw new RuntimeException("Job not found with ID: " + id);
    }

    // Delete job
    public void deleteJob(Long id) {
        if (jobRepository.existsById(id)) {
            // Cancel scheduled job first
            schedulingService.cancelJob(id);
            jobRepository.deleteById(id);
            logger.info("Job {} deleted successfully", id);
        } else {
            throw new RuntimeException("Job not found with ID: " + id);
        }
    }

    // Execute job immediately
    public JobResponse executeJobNow(Long id) {
        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isPresent()) {
            Job job = jobOpt.get();
            
            // Send to Kafka for immediate execution
            if (job instanceof BinaryJob) {
                kafkaProducerService.sendBinaryJobMessage((BinaryJob) job);
            } else if (job instanceof EmailJob) {
                kafkaProducerService.sendEmailJobMessage((EmailJob) job);
            }
            
            job.setStatus(Job.JobStatus.RUNNING);
            Job savedJob = jobRepository.save(job);
            
            logger.info("Job {} queued for immediate execution", id);
            return new JobResponse(savedJob);
        }
        throw new RuntimeException("Job not found with ID: " + id);
    }

    // Get job statistics
    public JobStatistics getJobStatistics() {
        JobStatistics stats = new JobStatistics();
        stats.setPendingCount(jobRepository.countByStatus(Job.JobStatus.PENDING));
        stats.setRunningCount(jobRepository.countByStatus(Job.JobStatus.RUNNING));
        stats.setCompletedCount(jobRepository.countByStatus(Job.JobStatus.COMPLETED));
        stats.setFailedCount(jobRepository.countByStatus(Job.JobStatus.FAILED));
        stats.setCancelledCount(jobRepository.countByStatus(Job.JobStatus.CANCELLED));
        stats.setTotalCount(jobRepository.count());
        return stats;
    }

    // Helper method to calculate next run time
    private LocalDateTime calculateNextRun(LocalDateTime scheduledTime, Job.RepeatPattern repeatPattern) {
        switch (repeatPattern) {
            case DAILY:
                return scheduledTime.plusDays(1);
            case WEEKLY:
                return scheduledTime.plusWeeks(1);
            case MONTHLY:
                return scheduledTime.plusMonths(1);
            case YEARLY:
                return scheduledTime.plusYears(1);
            default:
                return null;
        }
    }

    // Inner class for job statistics
    public static class JobStatistics {
        private long pendingCount;
        private long runningCount;
        private long completedCount;
        private long failedCount;
        private long cancelledCount;
        private long totalCount;

        // Getters and Setters
        public long getPendingCount() { return pendingCount; }
        public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }

        public long getRunningCount() { return runningCount; }
        public void setRunningCount(long runningCount) { this.runningCount = runningCount; }

        public long getCompletedCount() { return completedCount; }
        public void setCompletedCount(long completedCount) { this.completedCount = completedCount; }

        public long getFailedCount() { return failedCount; }
        public void setFailedCount(long failedCount) { this.failedCount = failedCount; }

        public long getCancelledCount() { return cancelledCount; }
        public void setCancelledCount(long cancelledCount) { this.cancelledCount = cancelledCount; }

        public long getTotalCount() { return totalCount; }
        public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    }
}