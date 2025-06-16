package com.jobscheduler.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("BINARY")
public class BinaryJob extends Job {

    @NotBlank
    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "presigned_url")
    private String presignedUrl;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "minio_bucket")
    private String minioBucket;

    @Column(name = "minio_object_name")
    private String minioObjectName;

    // Constructors
    public BinaryJob() {
        super();
    }

    public BinaryJob(String name, LocalDateTime scheduledTime, RepeatPattern repeatPattern, 
                     String timezone, String filePath, Long fileSize) {
        super(name, scheduledTime, repeatPattern, timezone);
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    @Override
    public JobType getJobType() {
        return JobType.BINARY;
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

    public String getMinioBucket() { return minioBucket; }
    public void setMinioBucket(String minioBucket) { this.minioBucket = minioBucket; }

    public String getMinioObjectName() { return minioObjectName; }
    public void setMinioObjectName(String minioObjectName) { this.minioObjectName = minioObjectName; }
}