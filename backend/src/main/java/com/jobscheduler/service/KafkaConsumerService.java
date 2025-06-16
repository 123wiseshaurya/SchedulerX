package com.jobscheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobscheduler.entity.BinaryJob;
import com.jobscheduler.entity.EmailJob;
import com.jobscheduler.entity.Job;
import com.jobscheduler.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private BinaryExecutionService binaryExecutionService;

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "${app.kafka.topics.binary-jobs}", groupId = "${app.kafka.consumer.group-id}")
    public void consumeBinaryJob(@Payload String message, 
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                               @Header(KafkaHeaders.OFFSET) long offset,
                               Acknowledgment acknowledgment) {
        
        logger.info("Received binary job message from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        
        try {
            BinaryJob job = objectMapper.readValue(message, BinaryJob.class);
            
            // Update job status to RUNNING
            updateJobStatus(job.getId(), Job.JobStatus.RUNNING);
            
            // Execute the binary job
            boolean success = binaryExecutionService.executeBinaryJob(job);
            
            // Update job status based on execution result
            if (success) {
                updateJobStatus(job.getId(), Job.JobStatus.COMPLETED);
                logger.info("Binary job {} executed successfully", job.getId());
            } else {
                updateJobStatus(job.getId(), Job.JobStatus.FAILED);
                logger.error("Binary job {} execution failed", job.getId());
            }
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing binary job message: {}", message, e);
            // Don't acknowledge - message will be retried
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.email-jobs}", groupId = "${app.kafka.consumer.group-id}")
    public void consumeEmailJob(@Payload String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                              @Header(KafkaHeaders.OFFSET) long offset,
                              Acknowledgment acknowledgment) {
        
        logger.info("Received email job message from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        
        try {
            EmailJob job = objectMapper.readValue(message, EmailJob.class);
            
            // Update job status to RUNNING
            updateJobStatus(job.getId(), Job.JobStatus.RUNNING);
            
            // Send the email
            boolean success = emailService.sendEmail(job);
            
            // Update job status based on execution result
            if (success) {
                updateJobStatus(job.getId(), Job.JobStatus.COMPLETED);
                logger.info("Email job {} executed successfully", job.getId());
            } else {
                updateJobStatus(job.getId(), Job.JobStatus.FAILED);
                logger.error("Email job {} execution failed", job.getId());
            }
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing email job message: {}", message, e);
            // Don't acknowledge - message will be retried
        }
    }

    private void updateJobStatus(Long jobId, Job.JobStatus status) {
        try {
            jobRepository.findById(jobId).ifPresent(job -> {
                job.setStatus(status);
                if (status == Job.JobStatus.RUNNING) {
                    job.setLastRun(LocalDateTime.now());
                }
                job.setExecutionCount(job.getExecutionCount() + 1);
                jobRepository.save(job);
            });
        } catch (Exception e) {
            logger.error("Error updating job status for job ID: {}", jobId, e);
        }
    }
}