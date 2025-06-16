# ⚡ Kafka Configuration Guide

This guide explains how to set up and configure Apache Kafka for JobScheduler Pro.

## Overview

Kafka is used for asynchronous job processing in JobScheduler Pro. When jobs are scheduled, they are sent to Kafka topics and processed by consumers.

## Kafka Architecture in JobScheduler Pro

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Job Scheduler │───▶│  Kafka Topics   │───▶│  Job Consumers  │
│   (Producer)    │    │                 │    │   (Workers)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ├─ binary-jobs
                              ├─ email-jobs
                              └─ job-status-updates
```

## Topics Configuration

### 1. binary-jobs
- **Purpose**: Binary script execution jobs
- **Partitions**: 3 (for parallel processing)
- **Replication**: 1 (single broker setup)
- **Consumer Group**: `job-scheduler-group`

### 2. email-jobs
- **Purpose**: Email campaign jobs
- **Partitions**: 3
- **Replication**: 1
- **Consumer Group**: `job-scheduler-group`

### 3. job-status-updates
- **Purpose**: Job status change notifications
- **Partitions**: 1
- **Replication**: 1
- **Consumer Group**: `status-update-group`

## Docker Configuration

### docker-compose.yml
```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    hostname: kafka
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "9101:9101"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_JMX_PORT: 9101
      KAFKA_JMX_HOSTNAME: localhost
```

## Spring Boot Configuration

### application.yml
```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      acks: all
      retries: 3
      enable-idempotence: true
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      auto-offset-reset: earliest
      enable-auto-commit: false
      max-poll-records: 10

app:
  kafka:
    topics:
      binary-jobs: ${KAFKA_TOPIC_BINARY:binary-jobs}
      email-jobs: ${KAFKA_TOPIC_EMAIL:email-jobs}
      job-status-updates: ${KAFKA_TOPIC_STATUS:job-status-updates}
    consumer:
      group-id: ${KAFKA_GROUP_ID:job-scheduler-group}
```

## Starting Kafka

### Using Docker Compose
```bash
cd backend
docker-compose up -d zookeeper kafka
```

### Verify Kafka is Running
```bash
# Check container status
docker-compose ps

# Check Kafka logs
docker-compose logs kafka

# Test Kafka connectivity
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

## Topic Management

### Create Topics Manually (Optional)
```bash
# Binary jobs topic
docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic binary-jobs \
  --partitions 3 \
  --replication-factor 1

# Email jobs topic
docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic email-jobs \
  --partitions 3 \
  --replication-factor 1

# Status updates topic
docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic job-status-updates \
  --partitions 1 \
  --replication-factor 1
```

### List Topics
```bash
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### Describe Topic
```bash
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic binary-jobs
```

## Testing Kafka

### Producer Test
```bash
# Send test message to binary-jobs topic
docker exec -it kafka kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic binary-jobs
```

### Consumer Test
```bash
# Consume messages from binary-jobs topic
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic binary-jobs \
  --from-beginning
```

### Test with Application
```bash
# Send test message via API
curl -X POST http://localhost:8080/api/jobs/binary \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Job",
    "filePath": "/tmp/test.py",
    "fileSize": 1024,
    "scheduledTime": "2024-12-28T10:00:00",
    "repeatPattern": "ONCE",
    "timezone": "Asia/Kolkata"
  }'
```

## Monitoring Kafka

### Consumer Groups
```bash
# List consumer groups
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list

# Describe consumer group
docker exec kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group job-scheduler-group
```

### Topic Statistics
```bash
# Get topic statistics
docker exec kafka kafka-run-class kafka.tools.GetOffsetShell \
  --broker-list localhost:9092 \
  --topic binary-jobs
```

### JMX Metrics (Port 9101)
```bash
# Connect JConsole or other JMX tools to localhost:9101
# Monitor metrics like:
# - kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec
# - kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec
```

## Troubleshooting Kafka

### Common Issues

#### 1. Kafka Not Starting
```bash
# Check Zookeeper first
docker-compose logs zookeeper

# Check Kafka logs
docker-compose logs kafka

# Restart services
docker-compose restart zookeeper kafka
```

#### 2. Connection Refused
```bash
# Check if Kafka is listening on port 9092
docker exec kafka netstat -tlnp | grep 9092

# Check advertised listeners
docker exec kafka cat /etc/kafka/server.properties | grep advertised
```

#### 3. Consumer Lag
```bash
# Check consumer lag
docker exec kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group job-scheduler-group
```

#### 4. Topic Not Found
```bash
# Auto-create topics is enabled by default
# Or create manually:
docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic missing-topic \
  --partitions 1 \
  --replication-factor 1
```

### Performance Tuning

#### Producer Configuration
```yaml
spring:
  kafka:
    producer:
      batch-size: 16384
      linger-ms: 5
      buffer-memory: 33554432
      compression-type: snappy
```

#### Consumer Configuration
```yaml
spring:
  kafka:
    consumer:
      fetch-min-size: 1
      fetch-max-wait: 500
      max-poll-records: 500
      session-timeout: 30000
```

## Production Configuration

### Security
```yaml
spring:
  kafka:
    security:
      protocol: SASL_SSL
    properties:
      sasl:
        mechanism: PLAIN
        jaas:
          config: org.apache.kafka.common.security.plain.PlainLoginModule required username="user" password="password";
      ssl:
        trust-store-location: /path/to/kafka.client.truststore.jks
        trust-store-password: password
```

### Multiple Brokers
```yaml
# Update docker-compose.yml for cluster setup
kafka-1:
  environment:
    KAFKA_BROKER_ID: 1
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-1:29092,PLAINTEXT_HOST://localhost:9092

kafka-2:
  environment:
    KAFKA_BROKER_ID: 2
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-2:29093,PLAINTEXT_HOST://localhost:9093
```

### Environment Variables
```bash
# Production Kafka cluster
KAFKA_BOOTSTRAP_SERVERS=kafka-1:9092,kafka-2:9092,kafka-3:9092

# Topic configuration
KAFKA_TOPIC_BINARY=prod-binary-jobs
KAFKA_TOPIC_EMAIL=prod-email-jobs
KAFKA_GROUP_ID=prod-job-scheduler-group
```

## Kafka UI (Optional)

### Add Kafka UI to docker-compose.yml
```yaml
kafka-ui:
  image: provectuslabs/kafka-ui:latest
  ports:
    - "8080:8080"
  environment:
    KAFKA_CLUSTERS_0_NAME: local
    KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
  depends_on:
    - kafka
```

Access Kafka UI at: http://localhost:8080

This completes the Kafka setup guide for JobScheduler Pro. Kafka will handle all asynchronous job processing efficiently.