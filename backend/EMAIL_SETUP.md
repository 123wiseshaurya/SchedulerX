# Email Configuration Setup

To enable email functionality in the Job Scheduler application, you need to configure SMTP settings.

## Gmail Setup (Recommended)

### Step 1: Enable 2-Factor Authentication
1. Go to your Google Account settings
2. Navigate to Security
3. Enable 2-Factor Authentication if not already enabled

### Step 2: Generate App Password
1. In Google Account Security settings
2. Click on "App passwords"
3. Select "Mail" as the app
4. Select "Other" as the device and name it "JobScheduler"
5. Copy the generated 16-character password

### Step 3: Configure Environment Variables
Create a `.env` file in the `backend` directory with:

```bash
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-character-app-password
EMAIL_ENABLED=true
```

### Step 4: Test Email Configuration
1. Start the backend application
2. Use the test endpoint:

```bash
curl -X POST http://localhost:8080/api/email/test \
  -H "Content-Type: application/json" \
  -d '{
    "to": "recipient@example.com",
    "subject": "Test Email from JobScheduler",
    "content": "This is a test email to verify the configuration."
  }'
```

## Other Email Providers

### Outlook/Hotmail
```bash
MAIL_HOST=smtp-mail.outlook.com
MAIL_PORT=587
MAIL_USERNAME=your-email@outlook.com
MAIL_PASSWORD=your-password
```

### Yahoo Mail
```bash
MAIL_HOST=smtp.mail.yahoo.com
MAIL_PORT=587
MAIL_USERNAME=your-email@yahoo.com
MAIL_PASSWORD=your-app-password
```

### Custom SMTP Server
```bash
MAIL_HOST=your-smtp-server.com
MAIL_PORT=587
MAIL_USERNAME=your-username
MAIL_PASSWORD=your-password
```

## Troubleshooting

### Common Issues:

1. **Authentication Failed**
   - Ensure you're using an App Password, not your regular password
   - Verify 2FA is enabled for Gmail

2. **Connection Timeout**
   - Check firewall settings
   - Verify SMTP server and port

3. **SSL/TLS Issues**
   - Ensure STARTTLS is enabled
   - Check if your email provider requires SSL

### Debug Mode
Enable debug logging by setting:
```bash
LOGGING_LEVEL_MAIL=DEBUG
```

### Test Configuration
Check if email is properly configured:
```bash
curl http://localhost:8080/api/email/config
```

## Security Notes

- Never commit your `.env` file to version control
- Use App Passwords instead of regular passwords
- Consider using environment-specific configurations
- Regularly rotate your App Passwords