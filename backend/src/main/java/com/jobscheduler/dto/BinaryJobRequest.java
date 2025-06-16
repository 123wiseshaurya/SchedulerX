package com.jobscheduler.dto;

import com.jobscheduler.entity.Job;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class BinaryJobRequest extends JobRequest {

    @NotBlank(message = "File path is required")
    private String filePath;

    private Long fileSize;
    private String presignedUrl;
    private String originalFilename;
    private String contentType;

    // Constructors
    public BinaryJobRequest() {
        super();
    }

    public BinaryJobRequest(String name, LocalDateTime scheduledTime, Job.RepeatPattern repeatPattern, 
                           String timezone, String filePath, Long fileSize) {
        super(name, scheduledTime, repeatPattern, timezone);
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getPresignedUrl() { return presignedUrl; }
    public void setPresignedUrl(String presignedUrl) { this.presignedUrl = presignedUrl; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}