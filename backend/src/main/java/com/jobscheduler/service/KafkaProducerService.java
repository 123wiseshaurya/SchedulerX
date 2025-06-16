package com.jobscheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobscheduler.entity.BinaryJob;
import com.jobscheduler.entity.EmailJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.kafka.topics.binary-jobs}")
    private String binaryJobsTopic;

    @Value("${app.kafka.topics.email-jobs}")
    private String emailJobsTopic;

    public void sendBinaryJobMessage(BinaryJob job) {
        try {
            String message = objectMapper.writeValueAsString(job);
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(binaryJobsTopic, job.getId().toString(), message);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Binary job message sent successfully for job ID: {} to topic: {}", 
                              job.getId(), binaryJobsTopic);
                } else {
                    logger.error("Failed to send binary job message for job ID: {}", job.getId(), exception);
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("Error serializing binary job message for job ID: {}", job.getId(), e);
        }
    }

    public void sendEmailJobMessage(EmailJob job) {
        try {
            String message = objectMapper.writeValueAsString(job);
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(emailJobsTopic, job.getId().toString(), message);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Email job message sent successfully for job ID: {} to topic: {}", 
                              job.getId(), emailJobsTopic);
                } else {
                    logger.error("Failed to send email job message for job ID: {}", job.getId(), exception);
                }
            });
        } catch (JsonProcessingException e) {
            logger.error("Error serializing email job message for job ID: {}", job.getId(), e);
        }
    }

    public void sendJobStatusUpdate(Long jobId, String status) {
        try {
            String message = String.format("{\"jobId\":%d,\"status\":\"%s\",\"timestamp\":\"%s\"}", 
                                         jobId, status, java.time.Instant.now());
            kafkaTemplate.send("job-status-updates", jobId.toString(), message);
            logger.info("Job status update sent for job ID: {} with status: {}", jobId, status);
        } catch (Exception e) {
            logger.error("Error sending job status update for job ID: {}", jobId, e);
        }
    }
}