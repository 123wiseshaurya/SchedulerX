package com.jobscheduler.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("EMAIL")
public class EmailJob extends Job {

    @ElementCollection
    @CollectionTable(name = "email_recipients", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "recipient_email")
    @NotEmpty
    private List<String> recipients = new ArrayList<>();

    @NotBlank
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotBlank
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "template")
    private String template = "default";

    @ElementCollection
    @CollectionTable(name = "email_attachments", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "attachment_path")
    private List<String> attachments = new ArrayList<>();

    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "sender_name")
    private String senderName;

    // Constructors
    public EmailJob() {
        super();
    }

    public EmailJob(String name, LocalDateTime scheduledTime, RepeatPattern repeatPattern, 
                    String timezone, List<String> recipients, String subject, String content) {
        super(name, scheduledTime, repeatPattern, timezone);
        this.recipients = recipients;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public JobType getJobType() {
        return JobType.EMAIL;
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