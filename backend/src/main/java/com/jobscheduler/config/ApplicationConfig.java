package com.jobscheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationConfig {
    
    private String name = "JobScheduler Pro";
    private String version = "1.0.0";
    private Email email = new Email();
    private Minio minio = new Minio();
    private Kafka kafka = new Kafka();

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }

    public Minio getMinio() { return minio; }
    public void setMinio(Minio minio) { this.minio = minio; }

    public Kafka getKafka() { return kafka; }
    public void setKafka(Kafka kafka) { this.kafka = kafka; }

    public static class Email {
        private String defaultSenderName = "JobScheduler Pro";
        private boolean enabled = false;

        public String getDefaultSenderName() { return defaultSenderName; }
        public void setDefaultSenderName(String defaultSenderName) { this.defaultSenderName = defaultSenderName; }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    public static class Minio {
        private String endpoint = "http://localhost:9000";
        private String accessKey = "minioadmin";
        private String secretKey = "minioadmin";

        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

        public String getAccessKey() { return accessKey; }
        public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    }

    public static class Kafka {
        private Topics topics = new Topics();
        private Consumer consumer = new Consumer();

        public Topics getTopics() { return topics; }
        public void setTopics(Topics topics) { this.topics = topics; }

        public Consumer getConsumer() { return consumer; }
        public void setConsumer(Consumer consumer) { this.consumer = consumer; }

        public static class Topics {
            private String binaryJobs = "binary-jobs";
            private String emailJobs = "email-jobs";
            private String jobStatusUpdates = "job-status-updates";

            public String getBinaryJobs() { return binaryJobs; }
            public void setBinaryJobs(String binaryJobs) { this.binaryJobs = binaryJobs; }

            public String getEmailJobs() { return emailJobs; }
            public void setEmailJobs(String emailJobs) { this.emailJobs = emailJobs; }

            public String getJobStatusUpdates() { return jobStatusUpdates; }
            public void setJobStatusUpdates(String jobStatusUpdates) { this.jobStatusUpdates = jobStatusUpdates; }
        }

        public static class Consumer {
            private String groupId = "job-scheduler-group";

            public String getGroupId() { return groupId; }
            public void setGroupId(String groupId) { this.groupId = groupId; }
        }
    }
}