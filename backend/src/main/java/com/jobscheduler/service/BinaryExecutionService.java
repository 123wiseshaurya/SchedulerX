package com.jobscheduler.service;

import com.jobscheduler.entity.BinaryJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Service
public class BinaryExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(BinaryExecutionService.class);

    @Autowired
    private MinioService minioService;

    public boolean executeBinaryJob(BinaryJob job) {
        logger.info("Starting execution of binary job: {}", job.getId());

        try {
            // Download file from MinIO if needed
            Path localFilePath = downloadFileFromStorage(job);
            
            if (localFilePath == null || !Files.exists(localFilePath)) {
                logger.error("Binary file not found for job: {}", job.getId());
                return false;
            }

            // Make file executable (for Unix-like systems)
            makeFileExecutable(localFilePath);

            // Execute the binary
            boolean success = executeBinary(localFilePath, job);

            // Clean up temporary file
            cleanupTempFile(localFilePath);

            return success;

        } catch (Exception e) {
            logger.error("Error executing binary job: {}", job.getId(), e);
            return false;
        }
    }

    private Path downloadFileFromStorage(BinaryJob job) {
        try {
            if (job.getMinioBucket() != null && job.getMinioObjectName() != null) {
                // Download from MinIO
                return minioService.downloadFile(job.getMinioBucket(), job.getMinioObjectName());
            } else if (job.getFilePath() != null) {
                // Use local file path
                return Paths.get(job.getFilePath());
            }
            return null;
        } catch (Exception e) {
            logger.error("Error downloading file for job: {}", job.getId(), e);
            return null;
        }
    }

    private void makeFileExecutable(Path filePath) {
        try {
            File file = filePath.toFile();
            if (!file.setExecutable(true)) {
                logger.warn("Could not set executable permission for file: {}", filePath);
            }
        } catch (Exception e) {
            logger.warn("Error setting executable permission for file: {}", filePath, e);
        }
    }

    private boolean executeBinary(Path filePath, BinaryJob job) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // Determine execution command based on file type
            String fileName = filePath.getFileName().toString().toLowerCase();
            if (fileName.endsWith(".py")) {
                processBuilder.command("python3", filePath.toString());
            } else if (fileName.endsWith(".sh")) {
                processBuilder.command("bash", filePath.toString());
            } else if (fileName.endsWith(".jar")) {
                processBuilder.command("java", "-jar", filePath.toString());
            } else {
                // Assume it's a native executable
                processBuilder.command(filePath.toString());
            }

            processBuilder.directory(filePath.getParent().toFile());
            processBuilder.redirectErrorStream(true);

            logger.info("Executing command: {} for job: {}", processBuilder.command(), job.getId());

            Process process = processBuilder.start();

            // Read output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    logger.debug("Job {} output: {}", job.getId(), line);
                }
            }

            // Wait for process to complete with timeout
            boolean finished = process.waitFor(30, TimeUnit.MINUTES);
            
            if (!finished) {
                logger.error("Binary execution timed out for job: {}", job.getId());
                process.destroyForcibly();
                return false;
            }

            int exitCode = process.exitValue();
            logger.info("Binary execution completed for job: {} with exit code: {}", job.getId(), exitCode);

            return exitCode == 0;

        } catch (IOException | InterruptedException e) {
            logger.error("Error executing binary for job: {}", job.getId(), e);
            return false;
        }
    }

    private void cleanupTempFile(Path filePath) {
        try {
            if (filePath.toString().contains("/tmp/") || filePath.toString().contains("\\temp\\")) {
                Files.deleteIfExists(filePath);
                logger.debug("Cleaned up temporary file: {}", filePath);
            }
        } catch (IOException e) {
            logger.warn("Could not clean up temporary file: {}", filePath, e);
        }
    }
}