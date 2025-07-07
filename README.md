# JobScheduler Pro - Complete Enterprise Scheduling Platform

A fully functional job scheduling application with binary execution and script file execution abd automation  and email campaigns and its automation , built with Node.js backend and React frontend.

## 🚀 Quick Start (5 Minutes Setup)

## 🚀 Quick Start (5 Minutes Setup)

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose

### 1. Clone and Setup
```bash
git clone <your-repo-url>
cd job-scheduler-app
chmod +x setup.sh
./setup.sh
```

### 2. Configure Email (Optional but Recommended)
```bash
cd backend
cp .env.example .env
# Edit .env with your email credentials
```

### 3. Start Everything
```bash
./start.sh
```

The application will be available at:
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
- **MinIO Console**: http://localhost:9001

## 📧 Email Configuration

### Gmail Setup (Recommended)
1. Enable 2-Factor Authentication in your Google Account
2. Generate App Password:
   - Google Account → Security → App Passwords
   - Select "Mail" and "Other (Custom name)"
   - Copy the 16-character password

3. Update `backend/.env`:
```bash
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-character-app-password
EMAIL_ENABLED=true
```

4. Restart the application:
```bash
./restart.sh
```

## 🎯 Features

### ✅ Binary Job Scheduling
- Upload and execute Python, Shell, Java JAR files
- Secure file storage with MinIO
- Scheduled execution with cron patterns
- Real-time status tracking

### ✅ Email Campaign Management
- Multiple recipients support
- HTML and plain text emails
- Template system
- Scheduled delivery

### ✅ Advanced Scheduling
- One-time and recurring jobs
- Multiple time zones support
- Delay execution options
- Kafka-based job queuing

### ✅ Monitoring & Management
- Real-time job status dashboard
- Execution history tracking
- Email configuration testing
- System health monitoring

## 🛠 Development

### Backend Development
```bash
cd backend
mvn spring-boot:run
```

### Frontend Development
```bash
npm run dev
```

### Database Access
- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:jobscheduler`
- **Username**: `sa`
- **Password**: `password`

## 🐳 Docker Services

The application uses these services:
- **Kafka & Zookeeper**: Message queuing
- **MinIO**: File storage
- **PostgreSQL**: Production database (optional)
- **Redis**: Caching (optional)

## 📊 API Endpoints

### Job Management
- `POST /api/jobs/binary` - Create binary job
- `POST /api/jobs/email` - Create email job
- `GET /api/jobs` - List all jobs
- `PUT /api/jobs/{id}/status` - Update job status
- `DELETE /api/jobs/{id}` - Delete job

### File Management
- `POST /api/files/upload` - Upload binary file
- `GET /api/files/presigned-url` - Get download URL
- `DELETE /api/files` - Delete file

### Email Testing
- `POST /api/email/test` - Send test email
- `GET /api/email/config` - Check email configuration

## 🔧 Configuration

### Environment Variables
```bash
# Email Configuration
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
EMAIL_ENABLED=true

# MinIO Configuration
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin

# Database (Optional)
DB_URL=jdbc:postgresql://localhost:5432/jobscheduler
DB_USERNAME=jobscheduler
DB_PASSWORD=password
```

## 🚨 Troubleshooting

### Email Issues
1. **Not receiving emails**: Check email configuration in the "Email Test" tab
2. **Authentication failed**: Use App Password, not regular password
3. **Connection timeout**: Check firewall settings

### File Upload Issues
1. **Upload failed**: Ensure MinIO is running (`docker-compose ps`)
2. **File not found**: Check MinIO console at http://localhost:9001

### Job Execution Issues
1. **Jobs not running**: Check Kafka logs (`docker-compose logs kafka`)
2. **Status not updating**: Verify database connection

## 📝 Usage Examples

### Schedule a Python Script
1. Go to "Binary Jobs" tab
2. Upload your Python script
3. Set execution time
4. Choose repeat pattern
5. Click "Schedule Job"

### Send Email Campaign
1. Go to "Email Jobs" tab
2. Add recipients
3. Write your email content
4. Schedule delivery time
5. Click "Schedule Email"

### Test Email Configuration
1. Go to "Email Test" tab
2. Check configuration status
3. Send test email to verify setup

## 🔒 Security

- CORS configured for development
- File upload validation
- SQL injection protection
- Environment variable security

## 📈 Production Deployment

1. Update configuration for production
2. Use PostgreSQL instead of H2
3. Configure proper email credentials
4. Set up SSL/TLS
5. Enable monitoring and logging

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Add tests
5. Submit pull request

## 📄 License

MIT License - see LICENSE file for details.
