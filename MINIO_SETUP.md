# 📁 MinIO Object Storage Setup Guide

This guide explains how to set up and configure MinIO for secure file storage in JobScheduler Pro.

## Overview

MinIO is used to store binary files (scripts, executables) that are uploaded for job execution. It provides S3-compatible object storage with a web console for management.

## MinIO Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   File Upload   │───▶│  MinIO Storage  │───▶│  Job Execution  │
│   (Frontend)    │    │                 │    │   (Backend)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ├─ job-scheduler-files/
                              │  ├─ script1.py
                              │  ├─ app.jar
                              │  └─ backup.sh
                              └─ presigned URLs
```

## Docker Configuration

### docker-compose.yml
```yaml
minio:
  image: minio/minio:latest
  hostname: minio
  container_name: minio
  ports:
    - "9000:9000"    # API port
    - "9001:9001"    # Console port
  environment:
    MINIO_ROOT_USER: minioadmin
    MINIO_ROOT_PASSWORD: minioadmin
  command: server /data --console-address ":9001"
  volumes:
    - minio_data:/data

volumes:
  minio_data:
```

## Starting MinIO

### Using Docker Compose
```bash
cd backend
docker-compose up -d minio
```

### Verify MinIO is Running
```bash
# Check container status
docker-compose ps minio

# Check MinIO logs
docker-compose logs minio

# Test API endpoint
curl http://localhost:9000/minio/health/live
```

## Accessing MinIO

### Web Console
- **URL**: http://localhost:9001
- **Username**: `minioadmin`
- **Password**: `minioadmin`

### API Endpoint
- **URL**: http://localhost:9000

## Spring Boot Configuration

### application.yml
```yaml
app:
  minio:
    endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
    access-key: ${MINIO_ACCESS_KEY:minioadmin}
    secret-key: ${MINIO_SECRET_KEY:minioadmin}
```

### MinIO Client Configuration
```java
@Configuration
public class MinioConfig {
    
    @Value("${app.minio.endpoint}")
    private String endpoint;
    
    @Value("${app.minio.access-key}")
    private String accessKey;
    
    @Value("${app.minio.secret-key}")
    private String secretKey;
    
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
```

## Bucket Management

### Default Bucket
- **Name**: `job-scheduler-files`
- **Purpose**: Store uploaded binary files
- **Auto-created**: Yes (by application)

### Create Bucket Manually
```bash
# Using MinIO Client (mc)
docker run --rm -it --entrypoint=/bin/sh minio/mc

# Configure alias
mc alias set local http://localhost:9000 minioadmin minioadmin

# Create bucket
mc mb local/job-scheduler-files

# List buckets
mc ls local/
```

### Bucket Policies
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {"AWS": "*"},
      "Action": ["s3:GetObject"],
      "Resource": ["arn:aws:s3:::job-scheduler-files/*"]
    }
  ]
}
```

## File Operations

### Upload File via API
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@script.py" \
  -F "bucket=job-scheduler-files"
```

### Get Presigned URL
```bash
curl "http://localhost:8080/api/files/presigned-url?bucket=job-scheduler-files&objectName=script.py&expirySeconds=3600"
```

### Delete File
```bash
curl -X DELETE "http://localhost:8080/api/files?bucket=job-scheduler-files&objectName=script.py"
```

## Testing MinIO

### Upload Test File
```bash
# Create test file
echo "print('Hello from Python!')" > test.py

# Upload via API
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@test.py"
```

### Verify Upload
1. Go to MinIO Console: http://localhost:9001
2. Login with `minioadmin` / `minioadmin`
3. Navigate to `job-scheduler-files` bucket
4. Verify `test.py` is present

### Download Test
```bash
# Get presigned URL
PRESIGNED_URL=$(curl -s "http://localhost:8080/api/files/presigned-url?bucket=job-scheduler-files&objectName=test.py" | jq -r '.presignedUrl')

# Download file
curl "$PRESIGNED_URL" -o downloaded_test.py

# Verify content
cat downloaded_test.py
```

## Monitoring MinIO

### Health Check
```bash
# API health
curl http://localhost:9000/minio/health/live

# Detailed health
curl http://localhost:9000/minio/health/ready
```

### Storage Usage
```bash
# Using MinIO Client
docker run --rm -it --entrypoint=/bin/sh minio/mc
mc alias set local http://localhost:9000 minioadmin minioadmin

# Check disk usage
mc admin info local

# Bucket statistics
mc ls --recursive local/job-scheduler-files
```

### Logs
```bash
# Container logs
docker-compose logs minio

# Follow logs
docker-compose logs -f minio
```

## Security Configuration

### Change Default Credentials
```yaml
# In docker-compose.yml
environment:
  MINIO_ROOT_USER: your-admin-user
  MINIO_ROOT_PASSWORD: your-secure-password-min-8-chars
```

### Environment Variables
```bash
# Production configuration
MINIO_ENDPOINT=https://your-minio-server.com
MINIO_ACCESS_KEY=production_access_key
MINIO_SECRET_KEY=production_secret_key_min_8_chars
```

### TLS/SSL Configuration
```yaml
# For production with SSL
minio:
  image: minio/minio:latest
  ports:
    - "9000:9000"
    - "9001:9001"
  environment:
    MINIO_ROOT_USER: admin
    MINIO_ROOT_PASSWORD: securepassword
  command: server /data --console-address ":9001" --certs-dir /certs
  volumes:
    - minio_data:/data
    - ./certs:/certs
```

## Troubleshooting MinIO

### Common Issues

#### 1. MinIO Not Starting
```bash
# Check logs
docker-compose logs minio

# Check port conflicts
lsof -i :9000
lsof -i :9001

# Restart MinIO
docker-compose restart minio
```

#### 2. Access Denied
```bash
# Check credentials
docker exec minio printenv | grep MINIO

# Reset credentials
docker-compose down
docker volume rm backend_minio_data
docker-compose up -d minio
```

#### 3. Bucket Not Found
```bash
# List buckets
docker run --rm -it --entrypoint=/bin/sh minio/mc
mc alias set local http://localhost:9000 minioadmin minioadmin
mc ls local/

# Create bucket if missing
mc mb local/job-scheduler-files
```

#### 4. Upload Failures
```bash
# Check disk space
docker exec minio df -h /data

# Check file permissions
docker exec minio ls -la /data

# Check MinIO logs
docker-compose logs minio | grep ERROR
```

#### 5. Presigned URL Issues
```bash
# Check system time (important for presigned URLs)
date

# Test presigned URL generation
curl "http://localhost:8080/api/files/presigned-url?bucket=job-scheduler-files&objectName=test.txt"
```

## Performance Tuning

### Docker Configuration
```yaml
minio:
  image: minio/minio:latest
  deploy:
    resources:
      limits:
        memory: 1G
        cpus: '0.5'
  environment:
    MINIO_ROOT_USER: minioadmin
    MINIO_ROOT_PASSWORD: minioadmin
    # Performance settings
    MINIO_CACHE: "on"
    MINIO_CACHE_DRIVES: "/tmp/cache"
    MINIO_CACHE_EXCLUDE: "*.tmp"
```

### Application Configuration
```yaml
# Increase upload limits
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

# MinIO client timeouts
app:
  minio:
    connect-timeout: 10000
    write-timeout: 60000
    read-timeout: 60000
```

## Backup and Recovery

### Data Backup
```bash
# Backup MinIO data
docker run --rm -v backend_minio_data:/data -v $(pwd):/backup alpine \
  tar czf /backup/minio_backup.tar.gz -C /data .

# Restore MinIO data
docker run --rm -v backend_minio_data:/data -v $(pwd):/backup alpine \
  tar xzf /backup/minio_backup.tar.gz -C /data
```

### Configuration Backup
```bash
# Export bucket policies
mc admin policy list local

# Export user configurations
mc admin user list local
```

## Production Deployment

### Distributed MinIO
```yaml
# Multi-node MinIO setup
minio-1:
  image: minio/minio:latest
  command: server http://minio-{1...4}/data{1...2}
  environment:
    MINIO_ROOT_USER: admin
    MINIO_ROOT_PASSWORD: securepassword

minio-2:
  image: minio/minio:latest
  command: server http://minio-{1...4}/data{1...2}
  environment:
    MINIO_ROOT_USER: admin
    MINIO_ROOT_PASSWORD: securepassword
```

### Load Balancer Configuration
```nginx
upstream minio {
    server minio-1:9000;
    server minio-2:9000;
    server minio-3:9000;
    server minio-4:9000;
}

server {
    listen 80;
    server_name minio.example.com;
    
    location / {
        proxy_pass http://minio;
        proxy_set_header Host $http_host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## MinIO Client (mc) Commands

### Installation
```bash
# Download MinIO client
wget https://dl.min.io/client/mc/release/linux-amd64/mc
chmod +x mc
sudo mv mc /usr/local/bin/
```

### Common Commands
```bash
# Configure alias
mc alias set local http://localhost:9000 minioadmin minioadmin

# List buckets
mc ls local/

# Copy files
mc cp file.txt local/job-scheduler-files/

# Mirror directories
mc mirror ./uploads/ local/job-scheduler-files/

# Set bucket policy
mc policy set public local/job-scheduler-files

# Monitor events
mc events add local/job-scheduler-files arn:minio:sqs::1:webhook --event put
```

This completes the MinIO setup guide for JobScheduler Pro. MinIO provides secure, scalable object storage for all your binary files.