package com.jobscheduler.controller;

import com.jobscheduler.config.ApplicationConfig;
import com.jobscheduler.service.EmailService;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthController {

    @Autowired
    private ApplicationConfig appConfig;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Application info
        health.put("application", appConfig.getName());
        health.put("version", appConfig.getVersion());
        health.put("profile", activeProfile);
        health.put("timestamp", LocalDateTime.now());
        health.put("status", "UP");

        // Service status
        Map<String, Object> services = new HashMap<>();
        
        // Email service
        services.put("email", Map.of(
            "configured", emailService.isConfigured(),
            "enabled", appConfig.getEmail().isEnabled()
        ));

        // MinIO service
        try {
            minioClient.listBuckets();
            services.put("minio", Map.of("status", "UP"));
        } catch (Exception e) {
            services.put("minio", Map.of("status", "DOWN", "error", e.getMessage()));
        }

        // Kafka service
        try {
            kafkaTemplate.send("health-check", "ping");
            services.put("kafka", Map.of("status", "UP"));
        } catch (Exception e) {
            services.put("kafka", Map.of("status", "DOWN", "error", e.getMessage()));
        }

        health.put("services", services);

        return ResponseEntity.ok(health);
    }

    @GetMapping("/simple")
    public ResponseEntity<Map<String, String>> getSimpleHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", appConfig.getName());
        health.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
}