# 🚀 Complete Setup Guide for JobScheduler Pro

## Prerequisites

Before starting, ensure you have these installed:

### Required Software
- **Java 17+** - [Download OpenJDK](https://adoptium.net/)
- **Node.js 18+** - [Download Node.js](https://nodejs.org/)
- **Maven 3.6+** - [Download Maven](https://maven.apache.org/download.cgi)
- **Docker & Docker Compose** - [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)
- **Git** - [Download Git](https://git-scm.com/downloads)

### Verify Installation
```bash
java -version    # Should show Java 17+
node -v         # Should show Node 18+
mvn -v          # Should show Maven 3.6+
docker -v       # Should show Docker version
git --version   # Should show Git version
```

## Step 1: Clone the Repository

```bash
# Clone the repository
git clone <your-repository-url>
cd jobscheduler-pro

# Make setup scripts executable (Linux/Mac)
chmod +x *.sh

# For Windows users, use Git Bash or WSL
```

## Step 2: Quick Setup (Automated)

### Option A: One-Command Setup
```bash
./setup.sh
```

### Option B: Manual Setup

#### 2.1 Install Dependencies
```bash
# Frontend dependencies
npm install

# Backend dependencies
cd backend
mvn clean install -DskipTests
cd ..
```

#### 2.2 Start Infrastructure Services
```bash
cd backend
docker-compose up -d
cd ..

# Wait for services to start
sleep 30

# Verify services are running
cd backend
docker-compose ps
cd ..
```

## Step 3: Configure Email (Optional but Recommended)

### 3.1 Gmail Configuration (Recommended)

1. **Enable 2-Factor Authentication**
   - Go to [Google Account Security](https://myaccount.google.com/security)
   - Enable 2-Factor Authentication if not already enabled

2. **Generate App Password**
   - In Google Account Security → App Passwords
   - Select "Mail" as app, "Other" as device
   - Name it "JobScheduler Pro"
   - Copy the 16-character password

3. **Configure Environment Variables**
```bash
cd backend
cp .env.example .env

# Edit .env file with your credentials:
# MAIL_USERNAME=your-email@gmail.com
# MAIL_PASSWORD=your-16-character-app-password
# EMAIL_ENABLED=true
```

### 3.2 Other Email Providers

#### Outlook/Hotmail
```bash
MAIL_HOST=smtp-mail.outlook.com
MAIL_PORT=587
MAIL_USERNAME=your-email@outlook.com
MAIL_PASSWORD=your-password
EMAIL_ENABLED=true
```

#### Yahoo Mail
```bash
MAIL_HOST=smtp.mail.yahoo.com
MAIL_PORT=587
MAIL_USERNAME=your-email@yahoo.com
MAIL_PASSWORD=your-app-password
EMAIL_ENABLED=true
```

## Step 4: Start the Application

### Option A: Automated Start
```bash
./start.sh
```

### Option B: Manual Start

#### 4.1 Start Backend
```bash
cd backend
mvn spring-boot:run &
cd ..
```

#### 4.2 Start Frontend
```bash
npm run dev
```

## Step 5: Access the Application

Once started, access these URLs:

- **🌐 Frontend Application**: http://localhost:5173
- **🔧 Backend API**: http://localhost:8080
- **💾 H2 Database Console**: http://localhost:8080/h2-console
- **📁 MinIO Console**: http://localhost:9001 (admin/minioadmin)
- **📊 Health Check**: http://localhost:8080/api/health

### Database Console Access
- **JDBC URL**: `jdbc:h2:mem:jobscheduler`
- **Username**: `sa`
- **Password**: `password`

## Step 6: Test the Setup

### 6.1 Test Email Configuration
1. Go to http://localhost:5173
2. Click on "Email Test" tab
3. Check configuration status
4. Send a test email to verify setup

### 6.2 Test Binary Job
1. Go to "Binary Jobs" tab
2. Upload a simple script (e.g., Python script)
3. Schedule it for immediate execution
4. Monitor execution in the jobs list

### 6.3 Test Email Job
1. Go to "Email Jobs" tab
2. Add recipient email
3. Write email content
4. Schedule for immediate delivery
5. Check recipient's inbox

## Troubleshooting

### Common Issues and Solutions

#### 1. Port Already in Use
```bash
# Check what's using the port
lsof -i :8080  # Backend port
lsof -i :5173  # Frontend port
lsof -i :9092  # Kafka port

# Kill the process if needed
kill -9 <PID>
```

#### 2. Docker Services Not Starting
```bash
# Check Docker status
docker ps

# Restart Docker services
cd backend
docker-compose down
docker-compose up -d

# Check logs
docker-compose logs kafka
docker-compose logs minio
```

#### 3. Email Not Working
```bash
# Check email configuration
curl http://localhost:8080/api/email/config

# Test email endpoint
curl -X POST http://localhost:8080/api/email/test \
  -H "Content-Type: application/json" \
  -d '{
    "to": "test@example.com",
    "subject": "Test",
    "content": "Test email"
  }'
```

#### 4. Backend Not Starting
```bash
# Check Java version
java -version

# Check if port 8080 is free
netstat -an | grep 8080

# Check backend logs
cd backend
tail -f backend.log
```

#### 5. Frontend Not Loading
```bash
# Check Node.js version
node -v

# Clear npm cache
npm cache clean --force

# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
```

### Service-Specific Troubleshooting

#### Kafka Issues
```bash
# Check Kafka logs
cd backend
docker-compose logs kafka

# Restart Kafka
docker-compose restart kafka zookeeper
```

#### MinIO Issues
```bash
# Check MinIO logs
cd backend
docker-compose logs minio

# Access MinIO console
# URL: http://localhost:9001
# Username: minioadmin
# Password: minioadmin
```

#### Database Issues
```bash
# For H2 (development)
# Access console: http://localhost:8080/h2-console

# For PostgreSQL (production)
# Update backend/.env with PostgreSQL credentials
```

## Production Deployment

### Environment Variables for Production
```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/jobscheduler
DB_USERNAME=jobscheduler
DB_PASSWORD=secure_password

# Email
MAIL_USERNAME=production-email@company.com
MAIL_PASSWORD=secure_app_password
EMAIL_ENABLED=true

# MinIO
MINIO_ENDPOINT=https://your-minio-server.com
MINIO_ACCESS_KEY=production_access_key
MINIO_SECRET_KEY=production_secret_key

# Security
SPRING_PROFILES_ACTIVE=production
LOG_LEVEL=WARN
```

### Build for Production
```bash
# Build frontend
npm run build

# Build backend
cd backend
mvn clean package -DskipTests
cd ..
```

## Stopping the Application

### Stop Everything
```bash
./stop.sh
```

### Manual Stop
```bash
# Stop frontend (Ctrl+C in terminal)
# Stop backend (Ctrl+C in terminal)

# Stop Docker services
cd backend
docker-compose down
cd ..
```

## Restart Application
```bash
./restart.sh
```

## Getting Help

### Check Application Status
```bash
# Health check
curl http://localhost:8080/api/health

# Simple health check
curl http://localhost:8080/api/health/simple
```

### View Logs
```bash
# Backend logs
cd backend
tail -f backend.log

# Docker service logs
docker-compose logs -f kafka
docker-compose logs -f minio
```

### Reset Everything
```bash
# Stop all services
./stop.sh

# Remove Docker volumes (WARNING: This deletes all data)
cd backend
docker-compose down -v

# Restart setup
cd ..
./setup.sh
```

## Next Steps

1. **Configure Email**: Set up email credentials for full functionality
2. **Create Jobs**: Start scheduling binary scripts and email campaigns
3. **Monitor**: Use the dashboard to track job execution
4. **Scale**: Configure for production use with PostgreSQL and proper security

## Support

- Check the application logs for detailed error messages
- Verify all prerequisites are installed correctly
- Ensure all ports (5173, 8080, 9000, 9092) are available
- Test each service individually if issues persist

Happy scheduling! 🚀