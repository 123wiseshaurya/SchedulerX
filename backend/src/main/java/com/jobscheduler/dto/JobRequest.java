package com.jobscheduler.dto;

import com.jobscheduler.entity.Job;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public abstract class JobRequest {

    @NotBlank(message = "Job name is required")
    private String name;

    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledTime;

    @NotNull(message = "Repeat pattern is required")
    private Job.RepeatPattern repeatPattern = Job.RepeatPattern.ONCE;

    @NotBlank(message = "Timezone is required")
    private String timezone = "Asia/Kolkata";

    private Integer delayMinutes = 0;

    // Constructors
    public JobRequest() {}

    public JobRequest(String name, LocalDateTime scheduledTime, Job.RepeatPattern repeatPattern, String timezone) {
        this.name = name;
        this.scheduledTime = scheduledTime;
        this.repeatPattern = repeatPattern;
        this.timezone = timezone;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public Job.RepeatPattern getRepeatPattern() { return repeatPattern; }
    public void setRepeatPattern(Job.RepeatPattern repeatPattern) { this.repeatPattern = repeatPattern; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Integer getDelayMinutes() { return delayMinutes; }
    public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }
}