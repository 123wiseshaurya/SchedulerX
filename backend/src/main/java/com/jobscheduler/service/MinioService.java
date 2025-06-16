package com.jobscheduler.service;

import io.minio.*;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    @Autowired
    private MinioClient minioClient;

    private static final String DEFAULT_BUCKET = "job-scheduler-files";

    public String uploadFile(MultipartFile file, String bucketName) throws Exception {
        if (bucketName == null) {
            bucketName = DEFAULT_BUCKET;
        }

        // Ensure bucket exists
        createBucketIfNotExists(bucketName);

        // Generate unique object name
        String objectName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            logger.info("File uploaded successfully: {} to bucket: {}", objectName, bucketName);
            return objectName;

        } catch (Exception e) {
            logger.error("Error uploading file to MinIO", e);
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    public Path downloadFile(String bucketName, String objectName) throws Exception {
        try {
            // Create temporary file
            Path tempFile = Files.createTempFile("job-scheduler-", "-" + objectName);

            // Download file from MinIO
            try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build())) {
                
                Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            logger.info("File downloaded successfully: {} from bucket: {}", objectName, bucketName);
            return tempFile;

        } catch (Exception e) {
            logger.error("Error downloading file from MinIO: {}/{}", bucketName, objectName, e);
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    public String getPresignedUrl(String bucketName, String objectName, int expiryInSeconds) throws Exception {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry(expiryInSeconds)
                    .build()
            );
        } catch (Exception e) {
            logger.error("Error generating presigned URL for {}/{}", bucketName, objectName, e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    public void deleteFile(String bucketName, String objectName) throws Exception {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            logger.info("File deleted successfully: {} from bucket: {}", objectName, bucketName);
        } catch (Exception e) {
            logger.error("Error deleting file from MinIO: {}/{}", bucketName, objectName, e);
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }

    private void createBucketIfNotExists(String bucketName) throws Exception {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                );
                logger.info("Bucket created: {}", bucketName);
            }
        } catch (Exception e) {
            logger.error("Error creating bucket: {}", bucketName, e);
            throw new RuntimeException("Failed to create bucket", e);
        }
    }
}