# 🪟 Windows Setup Guide for JobScheduler Pro

## Prerequisites for Windows

### Required Software
1. **Java 17+**
   - Download from [Adoptium](https://adoptium.net/)
   - Add to PATH: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot\bin`

2. **Node.js 18+**
   - Download from [nodejs.org](https://nodejs.org/)
   - Includes npm automatically

3. **Maven 3.6+**
   - Download from [maven.apache.org](https://maven.apache.org/download.cgi)
   - Extract to `C:\Program Files\Apache\maven`
   - Add to PATH: `C:\Program Files\Apache\maven\bin`

4. **Docker Desktop**
   - Download from [docker.com](https://www.docker.com/products/docker-desktop/)
   - Enable WSL 2 backend

5. **Git for Windows**
   - Download from [git-scm.com](https://git-scm.com/download/win)
   - Include Git Bash

### Verify Installation (Command Prompt or PowerShell)
```cmd
java -version
node -v
npm -v
mvn -v
docker -v
git --version
```

## Setup Options

### Option 1: Using Git Bash (Recommended)
```bash
# Open Git Bash as Administrator
git clone <your-repository-url>
cd jobscheduler-pro

# Run setup script
./setup.sh
```

### Option 2: Using Command Prompt/PowerShell

#### Step 1: Clone Repository
```cmd
git clone <your-repository-url>
cd jobscheduler-pro
```

#### Step 2: Install Dependencies
```cmd
REM Install frontend dependencies
npm install

REM Install backend dependencies
cd backend
mvn clean install -DskipTests
cd ..
```

#### Step 3: Start Docker Services
```cmd
cd backend
docker-compose up -d
cd ..

REM Wait for services to start
timeout /t 30
```

#### Step 4: Configure Email (Optional)
```cmd
cd backend
copy .env.example .env

REM Edit .env file with your email credentials
notepad .env
```

#### Step 5: Start Application

**Start Backend (in one terminal):**
```cmd
cd backend
mvn spring-boot:run
```

**Start Frontend (in another terminal):**
```cmd
npm run dev
```

## Windows-Specific Scripts

### setup.bat
```batch
@echo off
echo Setting up JobScheduler Pro on Windows...

echo Installing frontend dependencies...
npm install

echo Installing backend dependencies...
cd backend
mvn clean install -DskipTests
cd ..

echo Starting Docker services...
cd backend
docker-compose up -d
cd ..

echo Waiting for services to start...
timeout /t 30

echo Setup complete!
echo.
echo To configure email:
echo   1. cd backend
echo   2. copy .env.example .env
echo   3. Edit .env with your email credentials
echo.
echo Start the application with: start.bat
pause
```

### start.bat
```batch
@echo off
echo Starting JobScheduler Pro...

echo Starting Docker services...
cd backend
docker-compose up -d
cd ..

echo Starting backend server...
cd backend
start "Backend Server" mvn spring-boot:run
cd ..

echo Waiting for backend to start...
timeout /t 30

echo Starting frontend server...
start "Frontend Server" npm run dev

echo JobScheduler Pro is starting up!
echo.
echo Frontend: http://localhost:5173
echo Backend: http://localhost:8080
echo.
echo Press any key to continue...
pause
```

### stop.bat
```batch
@echo off
echo Stopping JobScheduler Pro...

echo Stopping Docker services...
cd backend
docker-compose down
cd ..

echo Stopping Java processes...
taskkill /f /im java.exe 2>nul

echo Stopping Node processes...
taskkill /f /im node.exe 2>nul

echo JobScheduler Pro stopped!
pause
```

## Windows Troubleshooting

### Common Windows Issues

#### 1. Port Already in Use
```cmd
REM Check what's using the port
netstat -ano | findstr :8080
netstat -ano | findstr :5173

REM Kill process by PID
taskkill /PID <PID> /F
```

#### 2. Docker Issues
```cmd
REM Restart Docker Desktop
REM Or use command line:
docker-compose down
docker-compose up -d
```

#### 3. Java/Maven Issues
```cmd
REM Check JAVA_HOME
echo %JAVA_HOME%

REM Check PATH
echo %PATH%

REM Set JAVA_HOME if needed
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot
```

#### 4. Permission Issues
- Run Command Prompt or PowerShell as Administrator
- Ensure Docker Desktop is running
- Check Windows Defender/Antivirus settings

#### 5. WSL Issues (if using Docker with WSL)
```cmd
REM Update WSL
wsl --update

REM Restart WSL
wsl --shutdown
```

### Windows Firewall Configuration

If you have issues accessing the application:

1. **Windows Defender Firewall**
   - Go to Control Panel → System and Security → Windows Defender Firewall
   - Click "Allow an app or feature through Windows Defender Firewall"
   - Add Java and Node.js if not present

2. **Port Configuration**
   - Ensure ports 5173, 8080, 9000, 9092 are not blocked
   - Add inbound rules for these ports if needed

### Environment Variables Setup

#### Using System Properties
1. Right-click "This PC" → Properties
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Add/Edit PATH to include:
   - Java: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot\bin`
   - Maven: `C:\Program Files\Apache\maven\bin`

#### Using Command Line
```cmd
REM Set JAVA_HOME
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot"

REM Add to PATH
setx PATH "%PATH%;%JAVA_HOME%\bin;C:\Program Files\Apache\maven\bin"
```

## Email Configuration on Windows

### Gmail Setup
1. Create `backend\.env` file:
```
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-character-app-password
EMAIL_ENABLED=true
```

2. Use Notepad or any text editor to edit the file

### Testing Email
```cmd
REM Test email configuration
curl -X GET http://localhost:8080/api/email/config

REM Send test email
curl -X POST http://localhost:8080/api/email/test ^
  -H "Content-Type: application/json" ^
  -d "{\"to\":\"test@example.com\",\"subject\":\"Test\",\"content\":\"Test email\"}"
```

## Performance Tips for Windows

1. **Disable Windows Defender Real-time Protection** temporarily during development
2. **Add project folder to exclusions** in Windows Defender
3. **Use SSD** for better performance
4. **Increase Docker memory allocation** in Docker Desktop settings
5. **Close unnecessary applications** to free up resources

## IDE Setup (Optional)

### IntelliJ IDEA
1. Import backend as Maven project
2. Set Project SDK to Java 17
3. Enable annotation processing
4. Install Spring Boot plugin

### Visual Studio Code
1. Install Java Extension Pack
2. Install Spring Boot Extension Pack
3. Install Node.js extensions
4. Open project folder

## Next Steps

1. **Verify Setup**: Access http://localhost:5173
2. **Configure Email**: Edit `backend\.env` file
3. **Test Features**: Try creating binary and email jobs
4. **Monitor**: Check system status in the application

## Getting Help

- Check Windows Event Viewer for system errors
- Use Task Manager to monitor resource usage
- Check Docker Desktop logs
- Review application logs in `backend\backend.log`

Happy scheduling on Windows! 🪟🚀