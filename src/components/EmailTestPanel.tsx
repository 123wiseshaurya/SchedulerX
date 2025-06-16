import React, { useState, useEffect } from 'react';
import { Mail, Send, CheckCircle, XCircle, AlertCircle, Settings } from 'lucide-react';

const EmailTestPanel: React.FC = () => {
  const [emailConfig, setEmailConfig] = useState<{
    configured: boolean;
    enabled: boolean;
    senderEmail: string;
  } | null>(null);
  
  const [testEmail, setTestEmail] = useState({
    to: '',
    subject: 'Test Email from JobScheduler',
    content: 'This is a test email to verify the email configuration is working correctly.'
  });
  
  const [sending, setSending] = useState(false);
  const [result, setResult] = useState<{
    success: boolean;
    message: string;
  } | null>(null);

  useEffect(() => {
    fetchEmailConfig();
  }, []);

  const fetchEmailConfig = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/email/config');
      const data = await response.json();
      setEmailConfig(data);
    } catch (error) {
      console.error('Failed to fetch email config:', error);
    }
  };

  const sendTestEmail = async () => {
    setSending(true);
    setResult(null);
    
    try {
      const response = await fetch('http://localhost:8080/api/email/test', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(testEmail),
      });
      
      const data = await response.json();
      setResult({
        success: data.success,
        message: data.success ? data.message : data.error
      });
    } catch (error) {
      setResult({
        success: false,
        message: 'Failed to send test email. Please check if the backend is running.'
      });
    } finally {
      setSending(false);
    }
  };

  if (!emailConfig) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div className="animate-pulse">
          <div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
          <div className="h-4 bg-gray-200 rounded w-1/2"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center space-x-3 mb-6">
        <div className="flex items-center justify-center w-10 h-10 bg-blue-100 rounded-lg">
          <Mail className="w-5 h-5 text-blue-600" />
        </div>
        <div>
          <h2 className="text-lg font-semibold text-gray-900">Email Configuration Test</h2>
          <p className="text-sm text-gray-500">Test your email settings</p>
        </div>
      </div>

      {/* Configuration Status */}
      <div className="mb-6 p-4 rounded-lg border">
        <div className="flex items-center space-x-2 mb-2">
          <Settings className="w-4 h-4 text-gray-600" />
          <span className="font-medium text-gray-900">Configuration Status</span>
        </div>
        
        <div className="space-y-2 text-sm">
          <div className="flex items-center space-x-2">
            {emailConfig.configured ? (
              <CheckCircle className="w-4 h-4 text-green-500" />
            ) : (
              <XCircle className="w-4 h-4 text-red-500" />
            )}
            <span>Email Configured: {emailConfig.configured ? 'Yes' : 'No'}</span>
          </div>
          
          <div className="flex items-center space-x-2">
            {emailConfig.enabled ? (
              <CheckCircle className="w-4 h-4 text-green-500" />
            ) : (
              <XCircle className="w-4 h-4 text-red-500" />
            )}
            <span>Email Enabled: {emailConfig.enabled ? 'Yes' : 'No'}</span>
          </div>
          
          <div className="flex items-center space-x-2">
            <Mail className="w-4 h-4 text-gray-500" />
            <span>Sender: {emailConfig.senderEmail}</span>
          </div>
        </div>
      </div>

      {!emailConfig.configured && (
        <div className="mb-6 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
          <div className="flex items-start space-x-2">
            <AlertCircle className="w-5 h-5 text-yellow-600 mt-0.5" />
            <div>
              <h3 className="font-medium text-yellow-800">Email Not Configured</h3>
              <p className="text-sm text-yellow-700 mt-1">
                To enable email functionality, please set up your SMTP credentials:
              </p>
              <ol className="text-sm text-yellow-700 mt-2 ml-4 list-decimal">
                <li>Create a <code className="bg-yellow-100 px-1 rounded">.env</code> file in the backend directory</li>
                <li>Add your email credentials (see EMAIL_SETUP.md for details)</li>
                <li>Restart the backend application</li>
              </ol>
            </div>
          </div>
        </div>
      )}

      {/* Test Email Form */}
      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Recipient Email
          </label>
          <input
            type="email"
            value={testEmail.to}
            onChange={(e) => setTestEmail(prev => ({ ...prev, to: e.target.value }))}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Enter recipient email"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Subject
          </label>
          <input
            type="text"
            value={testEmail.subject}
            onChange={(e) => setTestEmail(prev => ({ ...prev, subject: e.target.value }))}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Content
          </label>
          <textarea
            value={testEmail.content}
            onChange={(e) => setTestEmail(prev => ({ ...prev, content: e.target.value }))}
            rows={4}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <button
          onClick={sendTestEmail}
          disabled={sending || !emailConfig.configured || !testEmail.to}
          className="w-full bg-blue-600 text-white py-3 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 font-medium flex items-center justify-center space-x-2 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {sending ? (
            <>
              <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
              <span>Sending...</span>
            </>
          ) : (
            <>
              <Send className="w-4 h-4" />
              <span>Send Test Email</span>
            </>
          )}
        </button>

        {/* Result Display */}
        {result && (
          <div className={`p-4 rounded-lg border ${
            result.success 
              ? 'bg-green-50 border-green-200' 
              : 'bg-red-50 border-red-200'
          }`}>
            <div className="flex items-start space-x-2">
              {result.success ? (
                <CheckCircle className="w-5 h-5 text-green-600 mt-0.5" />
              ) : (
                <XCircle className="w-5 h-5 text-red-600 mt-0.5" />
              )}
              <div>
                <h3 className={`font-medium ${
                  result.success ? 'text-green-800' : 'text-red-800'
                }`}>
                  {result.success ? 'Success!' : 'Error'}
                </h3>
                <p className={`text-sm mt-1 ${
                  result.success ? 'text-green-700' : 'text-red-700'
                }`}>
                  {result.message}
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default EmailTestPanel;