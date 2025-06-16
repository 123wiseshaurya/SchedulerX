# 🐳 Docker Services Configuration

This document explains all the Docker services used by JobScheduler Pro and how to configure them.

## Services Overview

The application uses the following Docker services:

1. **Apache Kafka** - Message queuing for job processing
2. **Apache Zookeeper** - Kafka coordination service
3. **MinIO** - Object storage for binary files
4. **PostgreSQL** - Production database (optional)
5. **Redis** - Caching service (optional)

## Service Details

### 1. Apache Kafka & Zookeeper

**Purpose**: Asynchronous job processing and message queuing

**Ports**:
- Kafka: `9092` (external), `29092` (internal)
- Zookeeper: `2181`

**Configuration**:
```yaml
zookeeper:
  image: confluentinc/cp-zookeeper:7.4.0
  ports:
    - "2181:2181"
  environment:
    ZOOKEEPER_CLIENT_PORT: 2181
    ZOOKEEPER_TICK_TIME: 2000

kafka:
  image: confluentinc/cp-kafka:7.4.0
  ports:
    - "9092:9092"
  environment:
    KAFKA_BROKER_ID: 1
    KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
```

**Topics Created**:
- `binary-jobs` - Binary job execution messages
- `email-jobs` - Email job execution messages
- `job-status-updates` - Job status change notifications

### 2. MinIO Object Storage

**Purpose**: Secure storage for uploaded binary files

**Ports**:
- API: `9000`
- Console: `9001`

**Default Credentials**:
- Username: `minioadmin`
- Password: `minioadmin`

**Configuration**:
```yaml
minio:
  image: minio/minio:latest
  ports:
    - "9000:9000"
    - "9001:9001"
  environment:
    MINIO_ROOT_USER: minioadmin
    MINIO_ROOT_PASSWORD: minioadmin
  command: server /data --console-address ":9001"
```

**Buckets**:
- `job-scheduler-files` - Default bucket for binary files

### 3. PostgreSQL (Production)

**Purpose**: Production database for job persistence

**Port**: `5432`

**Configuration**:
```yaml
postgres:
  image: postgres:15-alpine
  ports:
    - "5432:5432"
  environment:
    POSTGRES_DB: jobscheduler
    POSTGRES_USER: jobscheduler
    POSTGRES_PASSWORD: password
```

### 4. Redis (Optional)

**Purpose**: Caching and session storage

**Port**: `6379`

**Configuration**:
```yaml
redis:
  image: redis:7-alpine
  ports:
    - "6379:6379"
```

## Managing Docker Services

### Start All Services
```bash
cd backend
docker-compose up -d
```

### Check Service Status
```bash
docker-compose ps
```

### View Service Logs
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs kafka
docker-compose logs minio
docker-compose logs postgres
```

### Stop All Services
```bash
docker-compose down
```

### Stop and Remove Volumes (⚠️ Deletes all data)
```bash
docker-compose down -v
```

### Restart Specific Service
```bash
docker-compose restart kafka
docker-compose restart minio
```

## Service Health Checks

### Kafka Health Check
```bash
# Check if Kafka is accepting connections
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### MinIO Health Check
```bash
# Access MinIO console
open http://localhost:9001

# Or check API
curl http://localhost:9000/minio/health/live
```

### PostgreSQL Health Check
```bash
# Connect to database
docker exec -it postgres psql -U jobscheduler -d jobscheduler

# Or check connection
docker exec postgres pg_isready -U jobscheduler
```

## Troubleshooting Docker Services

### Common Issues

#### 1. Port Conflicts
```bash
# Check what's using the port
lsof -i :9092  # Kafka
lsof -i :9000  # MinIO
lsof -i :5432  # PostgreSQL

# Kill conflicting process
kill -9 <PID>
```

#### 2. Services Not Starting
```bash
# Check Docker daemon
docker info

# Check available resources
docker system df

# Clean up unused resources
docker system prune
```

#### 3. Kafka Connection Issues
```bash
# Check Kafka logs
docker-compose logs kafka

# Verify Kafka is listening
docker exec kafka netstat -tlnp | grep 9092

# Test Kafka connectivity
docker exec kafka kafka-console-producer --bootstrap-server localhost:9092 --topic test
```

#### 4. MinIO Access Issues
```bash
# Check MinIO logs
docker-compose logs minio

# Verify MinIO is running
curl -I http://localhost:9000

# Reset MinIO credentials
docker-compose down
docker volume rm backend_minio_data
docker-compose up -d minio
```

#### 5. Database Connection Issues
```bash
# Check PostgreSQL logs
docker-compose logs postgres

# Test database connection
docker exec postgres psql -U jobscheduler -d jobscheduler -c "SELECT 1;"

# Reset database
docker-compose down
docker volume rm backend_postgres_data
docker-compose up -d postgres
```

## Production Configuration

### Environment Variables
```bash
# Kafka
KAFKA_BOOTSTRAP_SERVERS=your-kafka-cluster:9092

# MinIO
MINIO_ENDPOINT=https://your-minio-server.com
MINIO_ACCESS_KEY=production_access_key
MINIO_SECRET_KEY=production_secret_key

# PostgreSQL
DB_URL=jdbc:postgresql://your-db-server:5432/jobscheduler
DB_USERNAME=jobscheduler
DB_PASSWORD=secure_password
```

### Security Considerations

1. **Change Default Passwords**
   - MinIO: Update `MINIO_ROOT_USER` and `MINIO_ROOT_PASSWORD`
   - PostgreSQL: Use strong passwords

2. **Network Security**
   - Use Docker networks for service isolation
   - Expose only necessary ports
   - Use TLS/SSL for external connections

3. **Data Persistence**
   - Use named volumes for data persistence
   - Regular backups of PostgreSQL data
   - Monitor disk usage

### Scaling Services

#### Kafka Scaling
```yaml
# Add more Kafka brokers
kafka-2:
  image: confluentinc/cp-kafka:7.4.0
  environment:
    KAFKA_BROKER_ID: 2
    # ... other config
```

#### MinIO Scaling
```yaml
# MinIO cluster mode
minio-1:
  image: minio/minio:latest
  command: server http://minio-{1...4}/data{1...2}
```

## Monitoring Docker Services

### Resource Usage
```bash
# Monitor resource usage
docker stats

# Check disk usage
docker system df
```

### Service Logs
```bash
# Follow logs in real-time
docker-compose logs -f kafka
docker-compose logs -f minio

# View last N lines
docker-compose logs --tail=100 postgres
```

### Health Monitoring
```bash
# Check container health
docker-compose ps

# Inspect specific container
docker inspect backend_kafka_1
```

## Backup and Recovery

### PostgreSQL Backup
```bash
# Create backup
docker exec postgres pg_dump -U jobscheduler jobscheduler > backup.sql

# Restore backup
docker exec -i postgres psql -U jobscheduler jobscheduler < backup.sql
```

### MinIO Backup
```bash
# Use MinIO client (mc)
docker run --rm -it --entrypoint=/bin/sh minio/mc
mc alias set local http://localhost:9000 minioadmin minioadmin
mc cp --recursive local/job-scheduler-files/ ./backup/
```

### Volume Backup
```bash
# Backup Docker volumes
docker run --rm -v backend_postgres_data:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /data .
```

This completes the Docker services configuration guide. All services are configured to work together seamlessly for the JobScheduler Pro application.