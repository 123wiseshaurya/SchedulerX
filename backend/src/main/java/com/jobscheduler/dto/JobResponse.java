package com.jobscheduler.dto;

import com.jobscheduler.entity.Job;

import java.time.LocalDateTime;

public class JobResponse {

    private Long id;
    private String name;
    private Job.JobType type;
    private Job.JobStatus status;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastRun;
    private LocalDateTime nextRun;
    private Job.RepeatPattern repeatPattern;
    private String timezone;
    private Integer delayMinutes;
    private String errorMessage;
    private Integer executionCount;

    // Constructors
    public JobResponse() {}

    public JobResponse(Job job) {
        this.id = job.getId();
        this.name = job.getName();
        this.type = job.getJobType();
        this.status = job.getStatus();
        this.scheduledTime = job.getScheduledTime();
        this.createdAt = job.getCreatedAt();
        this.updatedAt = job.getUpdatedAt();
        this.lastRun = job.getLastRun();
        this.nextRun = job.getNextRun();
        this.repeatPattern = job.getRepeatPattern();
        this.timezone = job.getTimezone();
        this.delayMinutes = job.getDelayMinutes();
        this.errorMessage = job.getErrorMessage();
        this.executionCount = job.getExecutionCount();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Job.JobType getType() { return type; }
    public void setType(Job.JobType type) { this.type = type; }

    public Job.JobStatus getStatus() { return status; }
    public void setStatus(Job.JobStatus status) { this.status = status; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastRun() { return lastRun; }
    public void setLastRun(LocalDateTime lastRun) { this.lastRun = lastRun; }

    public LocalDateTime getNextRun() { return nextRun; }
    public void setNextRun(LocalDateTime nextRun) { this.nextRun = nextRun; }

    public Job.RepeatPattern getRepeatPattern() { return repeatPattern; }
    public void setRepeatPattern(Job.RepeatPattern repeatPattern) { this.repeatPattern = repeatPattern; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Integer getDelayMinutes() { return delayMinutes; }
    public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Integer getExecutionCount() { return executionCount; }
    public void setExecutionCount(Integer executionCount) { this.executionCount = executionCount; }
}