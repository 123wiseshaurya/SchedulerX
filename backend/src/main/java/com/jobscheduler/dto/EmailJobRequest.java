package com.jobscheduler.dto;

import com.jobscheduler.entity.Job;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public class EmailJobRequest extends JobRequest {

    @NotEmpty(message = "At least one recipient is required")
    private List<String> recipients;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    private String template = "default";
    private List<String> attachments;
    private String htmlContent;
    private String senderEmail;
    private String senderName;

    // Constructors
    public EmailJobRequest() {
        super();
    }

    public EmailJobRequest(String name, LocalDateTime scheduledTime, Job.RepeatPattern repeatPattern, 
                          String timezone, List<String> recipients, String subject, String content) {
        super(name, scheduledTime, repeatPattern, timezone);
        this.recipients = recipients;
        this.subject = subject;
        this.content = content;
    }

    // Getters and Setters
    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }

    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }

    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
}