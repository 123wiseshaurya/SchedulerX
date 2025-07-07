# SchedulerX - Complete Enterprise Scheduling Platform

A complete job scheduling system built with Node.js, React, Kafka, PostgreSQL, MinIO, and Redis for scheduling and processing email and binary jobs and script automation.


## Screenshots

![image](https://github.com/user-attachments/assets/967568b3-0e98-44b9-ba93-f545147ae139)

![image](https://github.com/user-attachments/assets/f064ed98-2552-450e-9c96-ac9f3c2f6a65)

![image](https://github.com/user-attachments/assets/6aa95b4a-e69c-4048-a507-51603c716c8b)

![image](https://github.com/user-attachments/assets/38eba12a-8fde-4c13-a1e5-331b483d0520)

MINIO CONSOLE👆
## 🚀 Quick Start

1. **Clone the repository**:
   ```bash
   git clone <your-repo-url>
   cd kafka-scheduling-system
   ```

2. **Start the system**:
   ```bash
   docker-compose up -d
   ```

3. **Wait for all services to start** (about 2-3 minutes for first run)

4. **Access the applications**:
   - **Frontend**: http://localhost:3000
   - **Backend API**: http://localhost:3001
   - **MinIO Console**: http://localhost:9001 (minioadmin/minioadmin123)
   - **Kafka UI**: http://localhost:8080
   - **PostgreSQL**: localhost:5432 (postgres/postgres123)

That's it! The system is ready to use.

## ✨ Features

- **Email Job Scheduling**: Schedule emails with attachments, CC/BCC, and HTML content
- **Binary Job Processing**: Schedule file processing jobs (compression, conversion, analysis)
- **Kafka Message Queue**: Reliable job processing with Kafka
- **Real-time Updates**: Live job status monitoring
- **File Management**: MinIO object storage for file uploads and downloads
- **Database**: PostgreSQL with proper schema and relationships
- **Caching**: Redis for session management and caching
- **Docker Support**: Complete containerized deployment

## 🏗️ System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React Frontend│    │   Node.js API   │    │   Kafka Cluster │
│   (Port 3000)   │────│   (Port 3001)   │────│   (Port 9092)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                │                       │
                       ┌─────────────────┐    ┌─────────────────┐
                       │   PostgreSQL    │    │   Job Processors│
                       │   (Port 5432)   │    │   (Consumers)   │
                       └─────────────────┘    └─────────────────┘
                                │
                       ┌─────────────────┐    ┌─────────────────┐
                       │     MinIO       │    │     Redis       │
                       │ (Ports 9000/1)  │    │   (Port 6379)   │
                       └─────────────────┘    └─────────────────┘
```

## 📋 Job Types

### Email Jobs
- Schedule emails with custom timing
- Support for attachments, CC/BCC
- HTML and plain text content
- SMTP configuration included

### Binary Jobs
- **Compression**: Gzip file compression
- **Conversion**: File format conversion
- **Analysis**: File metadata extraction
- **Custom Processing**: Extensible processing types

## 🔧 Configuration

The system is pre-configured with your settings:
- **SMTP**: rshauryasingh@gmail.com with app password
- **MinIO**: minioadmin/minioadmin123
- **Database**: postgres/postgres123
- **All services**: Ready to run out of the box

## 📡 API Endpoints

### Jobs
- `GET /api/jobs` - List all jobs with pagination
- `POST /api/jobs` - Create a new job
- `GET /api/jobs/:id` - Get job details
- `PATCH /api/jobs/:id/status` - Update job status
- `DELETE /api/jobs/:id` - Delete a job

### Email Jobs
- `GET /api/email` - List email jobs
- `POST /api/email` - Create email job
- `POST /api/email/test` - Send test email

### Binary Jobs
- `GET /api/binary` - List binary jobs
- `POST /api/binary` - Create binary job
- `GET /api/binary/processing-types` - Get available processing types

### File Management
- `POST /api/files/upload` - Upload file
- `GET /api/files/download/:filename` - Download file
- `GET /api/files/list` - List uploaded files
- `DELETE /api/files/:filename` - Delete file

## 🗄️ Database Schema

### Main Tables
- **jobs**: Core job information
- **email_jobs**: Email-specific data
- **binary_jobs**: Binary processing data
- **job_logs**: Execution logs and history

## 📊 Monitoring

### Kafka UI
Access Kafka topics, consumers, and messages at http://localhost:8080

### MinIO Console
Manage object storage at http://localhost:9001

### Application Logs
View logs with:
```bash
docker-compose logs -f [service-name]
```

## 🛠️ Development

### Local Development
1. **Start dependencies**:
   ```bash
   docker-compose up -d postgres kafka zookeeper minio redis
   ```

2. **Run backend**:
   ```bash
   cd backend
   npm install
   npm run dev
   ```

3. **Run frontend**:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## 🚨 Troubleshooting

### Common Issues
1. **Kafka Connection**: Ensure Kafka is fully started before backend
2. **File Uploads**: Check MinIO bucket permissions
3. **Email Sending**: Verify SMTP credentials and settings
4. **Database Connection**: Ensure PostgreSQL is ready

### Useful Commands
```bash
# View logs
docker-compose logs -f backend

# Restart specific service
docker-compose restart backend

# Check service status
docker-compose ps

# Access database
docker-compose exec postgres psql -U postgres -d schedulingdb

# Stop all services
docker-compose down

# Stop and remove volumes (fresh start)
docker-compose down -v
```

## 🔒 Production Deployment

### Security Considerations
1. Change default passwords
2. Use environment variables for secrets
3. Enable SSL/TLS
4. Configure proper firewall rules
5. Set up backup strategies

### Scaling
- **Horizontal Scaling**: Add more Kafka consumers
- **Database**: Consider read replicas for PostgreSQL
- **Load Balancing**: Use nginx for frontend/API load balancing
- **Monitoring**: Add Prometheus/Grafana for metrics

## 📝 License

MIT License - feel free to use this project for your own purposes.

## 🆘 Support

For issues and questions:
1. Check the logs: `docker-compose logs -f`
2. Ensure all services are running: `docker-compose ps`
3. Restart if needed: `docker-compose restart`

The system includes comprehensive error handling and logging to help diagnose problems.

## 🎯 Usage Examples

### Creating an Email Job
1. Go to http://localhost:3000
2. Click "Schedule Email" or navigate to Email Jobs
3. Fill in the form with recipient, subject, and message
4. Set the schedule time
5. Click "Create Job"

### Creating a Binary Job
1. Go to File Manager and upload a file
2. Navigate to Binary Jobs
3. Select the uploaded file
4. Choose processing type (compress, convert, analyze)
5. Set schedule time and create the job

### Monitoring Jobs
- Dashboard shows real-time statistics
- Job list shows all jobs with status
- Click on any job to see detailed logs and status

The system automatically processes jobs at their scheduled time and provides real-time updates on job status.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Add tests
5. Submit pull request

## 📄 License

MIT License - see LICENSE file for details.
