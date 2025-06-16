package com.jobscheduler.service;

import com.jobscheduler.entity.EmailJob;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String defaultSenderEmail;

    @Value("${app.email.default-sender-name:JobScheduler Pro}")
    private String defaultSenderName;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    public boolean sendEmail(EmailJob emailJob) {
        if (!emailEnabled || defaultSenderEmail == null || defaultSenderEmail.isEmpty()) {
            logger.error("Email service is not configured. Please set MAIL_USERNAME and MAIL_PASSWORD environment variables.");
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set sender
            String senderEmail = emailJob.getSenderEmail() != null ? emailJob.getSenderEmail() : defaultSenderEmail;
            String senderName = emailJob.getSenderName() != null ? emailJob.getSenderName() : defaultSenderName;
            helper.setFrom(senderEmail, senderName);

            // Set recipients
            String[] recipients = emailJob.getRecipients().toArray(new String[0]);
            helper.setTo(recipients);

            // Set subject and content
            helper.setSubject(emailJob.getSubject());
            
            // Use HTML content if available, otherwise use plain text
            if (emailJob.getHtmlContent() != null && !emailJob.getHtmlContent().isEmpty()) {
                helper.setText(emailJob.getContent(), emailJob.getHtmlContent());
            } else {
                helper.setText(emailJob.getContent(), false);
            }

            // Add attachments if any
            if (emailJob.getAttachments() != null && !emailJob.getAttachments().isEmpty()) {
                for (String attachmentPath : emailJob.getAttachments()) {
                    File attachment = new File(attachmentPath);
                    if (attachment.exists()) {
                        helper.addAttachment(attachment.getName(), attachment);
                    } else {
                        logger.warn("Attachment file not found: {}", attachmentPath);
                    }
                }
            }

            // Send the email
            mailSender.send(message);
            
            logger.info("Email sent successfully for job ID: {} to {} recipients", 
                       emailJob.getId(), emailJob.getRecipients().size());
            return true;

        } catch (MessagingException e) {
            logger.error("Failed to send email for job ID: {}", emailJob.getId(), e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while sending email for job ID: {}", emailJob.getId(), e);
            return false;
        }
    }

    public boolean sendTestEmail(String to, String subject, String content) {
        if (!emailEnabled || defaultSenderEmail == null || defaultSenderEmail.isEmpty()) {
            logger.error("Email service is not configured. Please set MAIL_USERNAME and MAIL_PASSWORD environment variables.");
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(defaultSenderEmail, defaultSenderName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false);

            mailSender.send(message);
            logger.info("Test email sent successfully to: {}", to);
            return true;

        } catch (Exception e) {
            logger.error("Failed to send test email to: {}", to, e);
            return false;
        }
    }

    public boolean isConfigured() {
        return emailEnabled && defaultSenderEmail != null && !defaultSenderEmail.isEmpty();
    }
}