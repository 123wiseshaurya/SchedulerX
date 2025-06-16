# 🚀 Quick Start Guide

Get JobScheduler Pro running in 5 minutes!

## Step 1: Setup
```bash
chmod +x setup.sh
./setup.sh
```

## Step 2: Configure Email (Optional)
```bash
cd backend
cp .env.example .env
# Edit .env with your Gmail credentials
```

### Gmail Setup:
1. Enable 2-Factor Authentication
2. Generate App Password: Google Account → Security → App Passwords
3. Use the 16-character password in `.env`

## Step 3: Start Application
```bash
./start.sh
```

## Step 4: Access Application
- **Frontend**: http://localhost:5173
- **Backend**: http://localhost:8080
- **Database**: http://localhost:8080/h2-console

## Step 5: Test Email (If Configured)
1. Go to "Email Test" tab
2. Enter recipient email
3. Click "Send Test Email"

## 🎯 What You Can Do

### Schedule Binary Jobs
1. "Binary Jobs" tab
2. Upload script (Python, Shell, JAR)
3. Set schedule
4. Execute automatically

### Schedule Email Campaigns
1. "Email Jobs" tab
2. Add recipients
3. Write content
4. Schedule delivery

### Monitor Jobs
- View all jobs in dashboard
- Real-time status updates
- Execution history

## 🛑 Stop Application
```bash
./stop.sh
```

## 🔄 Restart Application
```bash
./restart.sh
```

That's it! You now have a fully functional job scheduler running locally.