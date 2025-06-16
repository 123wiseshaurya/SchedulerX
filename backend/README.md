# Job Scheduler Backend

A comprehensive Spring Boot backend application for scheduling and executing binary scripts and email campaigns using Kafka message queues and MinIO for file storage.

## Features

- **Binary Job Scheduling**: Upload and schedule script execution (Python, Shell, Java, etc.)
- **Email Campaign Scheduling**: Schedule email campaigns with multiple recipients
- **Kafka Integration**: Asynchronous job processing using Apache Kafka
- **MinIO File Storage**: Secure file storage for binary scripts
- **Quartz Scheduling**: Advanced scheduling with cron expressions and repeat patterns
- **RESTful APIs**: Complete REST API for job management
- **Database Persistence**: Job history and execution tracking
- **Email Service**: SMTP integration for email delivery

## Technology Stack

- **Spring Boot 3.2.1**
- **Apache Kafka** - Message queuing
- **MinIO** - Object storage
- **Quartz Scheduler** - Job scheduling
- **PostgreSQL/H2** - Database
- **Spring Mail** - Email service
- **Spring Security** - Security configuration

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose

### 1. Start Infrastructure Services

```bash
cd backend
docker-compose up -d
```

This starts:
- Kafka & Zookeeper (port 9092)
- MinIO (port 9000, console: 9001)
- PostgreSQL (port 5432)
- Redis (port 6379)

### 2. Configure Application

Update `src/main/resources/application.yml`:

```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-app-password
```

### 3. Run Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Job Management

#### Create Binary Job
```http
POST /api/jobs/binary
Content-Type: application/json

{
  "name": "Data Processing Script",
  "filePath": "/path/to/script.py",
  "fileSize": 1024,
  "scheduledTime": "2024-12-28T10:00:00",
  "repeatPattern": "DAILY",
  "timezone": "Asia/Kolkata",
  "delayMinutes": 0
}
```

#### Create Email Job
```http
POST /api/jobs/email
Content-Type: application/json

{
  "name": "Weekly Newsletter",
  "recipients": ["user1@example.com", "user2@example.com"],
  "subject": "Weekly Update",
  "content": "Hello, this is your weekly newsletter...",
  "template": "newsletter",
  "scheduledTime": "2024-12-28T09:00:00",
  "repeatPattern": "WEEKLY",
  "timezone": "Asia/Kolkata"
}
```

#### Get All Jobs
```http
GET /api/jobs
```

#### Get Job by ID
```http
GET /api/jobs/{id}
```

#### Update Job Status
```http
PUT /api/jobs/{id}/status?status=RUNNING
```

#### Execute Job Immediately
```http
POST /api/jobs/{id}/execute
```

#### Delete Job
```http
DELETE /api/jobs/{id}
```

#### Get Job Statistics
```http
GET /api/jobs/statistics
```

### File Management

#### Upload File
```http
POST /api/files/upload
Content-Type: multipart/form-data

file: [binary file]
bucket: job-scheduler-files (optional)
```

#### Get Presigned URL
```http
GET /api/files/presigned-url?bucket=job-scheduler-files&objectName=script.py&expirySeconds=3600
```

#### Delete File
```http
DELETE /api/files?bucket=job-scheduler-files&objectName=script.py
```

## Configuration

### Database Configuration

For PostgreSQL (production):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/jobscheduler
    username: jobscheduler
    password: password
  jpa:
    hibernate:
      ddl-auto: validate
```

### Kafka Configuration

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
app:
  kafka:
    topics:
      binary-jobs: binary-jobs
      email-jobs: email-jobs
    consumer:
      group-id: job-scheduler-group
```

### MinIO Configuration

```yaml
app:
  minio:
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin
```

### Email Configuration

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## Job Types

### Binary Jobs
- Support for Python, Shell, Java JAR, and native executables
- File upload to MinIO with presigned URLs
- Execution timeout and error handling
- Output capture and logging

### Email Jobs
- Multiple recipients support
- HTML and plain text content
- Email templates
- Attachment support
- SMTP configuration

## Scheduling Patterns

- **ONCE**: Execute once at scheduled time
- **DAILY**: Repeat every day
- **WEEKLY**: Repeat every week
- **MONTHLY**: Repeat every month
- **YEARLY**: Repeat every year

## Monitoring

### Health Check
```http
GET /actuator/health
```

### Metrics
```http
GET /actuator/metrics
```

### Database Console (H2)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:jobscheduler`
- Username: `sa`
- Password: `password`

### MinIO Console
- URL: `http://localhost:9001`
- Username: `minioadmin`
- Password: `minioadmin`

## Development

### Running Tests
```bash
mvn test
```

### Building
```bash
mvn clean package
```

### Docker Build
```bash
docker build -t job-scheduler-backend .
```

## Production Deployment

1. Update configuration for production environment
2. Use PostgreSQL instead of H2
3. Configure proper email credentials
4. Set up Kafka cluster
5. Configure MinIO with proper security
6. Enable SSL/TLS
7. Set up monitoring and logging

## Troubleshooting

### Common Issues

1. **Kafka Connection Issues**
   - Ensure Kafka is running: `docker-compose ps`
   - Check Kafka logs: `docker-compose logs kafka`

2. **Email Not Sending**
   - Verify SMTP credentials
   - Check Gmail app password setup
   - Review email service logs

3. **File Upload Issues**
   - Verify MinIO is accessible
   - Check MinIO credentials
   - Ensure bucket permissions

4. **Job Not Executing**
   - Check Kafka consumer logs
   - Verify job status in database
   - Review Quartz scheduler logs

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.