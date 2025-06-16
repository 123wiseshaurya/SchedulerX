package com.jobscheduler.controller;

import com.jobscheduler.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username:}")
    private String configuredEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> sendTestEmail(@RequestBody TestEmailRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        if (!emailEnabled || configuredEmail.isEmpty()) {
            response.put("success", false);
            response.put("error", "Email service is not configured. Please set MAIL_USERNAME and MAIL_PASSWORD environment variables.");
            response.put("configured", false);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            boolean success = emailService.sendTestEmail(request.getTo(), request.getSubject(), request.getContent());
            
            if (success) {
                response.put("success", true);
                response.put("message", "Test email sent successfully to " + request.getTo());
            } else {
                response.put("success", false);
                response.put("error", "Failed to send test email. Check server logs for details.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Error sending test email: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getEmailConfig() {
        Map<String, Object> response = new HashMap<>();
        response.put("configured", !configuredEmail.isEmpty());
        response.put("enabled", emailEnabled);
        response.put("senderEmail", configuredEmail.isEmpty() ? "Not configured" : configuredEmail);
        return ResponseEntity.ok(response);
    }

    public static class TestEmailRequest {
        private String to;
        private String subject;
        private String content;

        // Getters and Setters
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}