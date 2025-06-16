package com.jobscheduler.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "job_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status = JobStatus.PENDING;

    @NotNull
    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_run")
    private LocalDateTime lastRun;

    @Column(name = "next_run")
    private LocalDateTime nextRun;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_pattern", nullable = false)
    private RepeatPattern repeatPattern = RepeatPattern.ONCE;

    @Column(name = "timezone", nullable = false)
    private String timezone = "Asia/Kolkata";

    @Column(name = "delay_minutes")
    private Integer delayMinutes = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "execution_count")
    private Integer executionCount = 0;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobExecution> executions = new ArrayList<>();

    // Constructors
    public Job() {}

    public Job(String name, LocalDateTime scheduledTime, RepeatPattern repeatPattern, String timezone) {
        this.name = name;
        this.scheduledTime = scheduledTime;
        this.repeatPattern = repeatPattern;
        this.timezone = timezone;
    }

    // Abstract method to get job type
    public abstract JobType getJobType();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

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

    public RepeatPattern getRepeatPattern() { return repeatPattern; }
    public void setRepeatPattern(RepeatPattern repeatPattern) { this.repeatPattern = repeatPattern; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Integer getDelayMinutes() { return delayMinutes; }
    public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Integer getExecutionCount() { return executionCount; }
    public void setExecutionCount(Integer executionCount) { this.executionCount = executionCount; }

    public List<JobExecution> getExecutions() { return executions; }
    public void setExecutions(List<JobExecution> executions) { this.executions = executions; }

    // Enums
    public enum JobStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }

    public enum RepeatPattern {
        ONCE, DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    }

    public enum JobType {
        BINARY, EMAIL
    }
}