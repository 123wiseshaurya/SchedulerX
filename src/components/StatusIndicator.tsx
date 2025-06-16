import React, { useState, useEffect } from 'react';
import { CheckCircle, XCircle, AlertCircle, Loader, Globe } from 'lucide-react';

interface SystemStatus {
  status: string;
  application: string;
  services: {
    email: { configured: boolean; enabled: boolean };
    minio: { status: string };
    kafka: { status: string };
  };
}

const StatusIndicator: React.FC = () => {
  const [status, setStatus] = useState<SystemStatus | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchStatus();
    const interval = setInterval(fetchStatus, 30000); // Check every 30 seconds
    return () => clearInterval(interval);
  }, []);

  const fetchStatus = async () => {
    try {
      setError(null);
      const response = await fetch('http://localhost:8080/api/health');
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      const data = await response.json();
      setStatus(data);
    } catch (error) {
      console.error('Failed to fetch system status:', error);
      setStatus(null);
      
      // Check if this is a mixed content error or network error from HTTPS to HTTP
      if (window.location.protocol === 'https:' && (
        error instanceof TypeError || 
        (error instanceof Error && error.message === 'Failed to fetch')
      )) {
        setError('mixed-content');
        // Automatically redirect to HTTP version
        window.location.href = 'http://localhost:5173';
      } else {
        setError('connection-failed');
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center space-x-2 px-3 py-1 bg-gray-50 rounded-full">
        <Loader className="w-4 h-4 text-gray-600 animate-spin" />
        <span className="text-sm font-medium text-gray-800">Checking...</span>
      </div>
    );
  }

  if (error === 'mixed-content') {
    return (
      <div className="flex items-center space-x-2 px-3 py-1 bg-amber-50 rounded-full cursor-pointer hover:bg-amber-100 transition-colors" 
           onClick={() => window.location.href = 'http://localhost:5173'}
           title="Click to switch to HTTP version to connect to backend">
        <Globe className="w-4 h-4 text-amber-600" />
        <span className="text-sm font-medium text-amber-800">Switch to HTTP</span>
      </div>
    );
  }

  if (error === 'connection-failed' || !status) {
    return (
      <div className="flex items-center space-x-2 px-3 py-1 bg-red-50 rounded-full">
        <XCircle className="w-4 h-4 text-red-600" />
        <span className="text-sm font-medium text-red-800">Backend Offline</span>
      </div>
    );
  }

  const getOverallStatus = () => {
    const services = status.services;
    const emailOk = services.email.configured || !services.email.enabled;
    const minioOk = services.minio.status === 'UP';
    const kafkaOk = services.kafka.status === 'UP';

    if (minioOk && kafkaOk && emailOk) {
      return { 
        bgColor: 'bg-green-50', 
        textColor: 'text-green-800', 
        iconColor: 'text-green-600',
        text: 'All Systems Online', 
        icon: CheckCircle 
      };
    } else if (minioOk && kafkaOk) {
      return { 
        bgColor: 'bg-yellow-50', 
        textColor: 'text-yellow-800', 
        iconColor: 'text-yellow-600',
        text: 'Email Not Configured', 
        icon: AlertCircle 
      };
    } else {
      return { 
        bgColor: 'bg-red-50', 
        textColor: 'text-red-800', 
        iconColor: 'text-red-600',
        text: 'System Issues', 
        icon: XCircle 
      };
    }
  };

  const overallStatus = getOverallStatus();
  const StatusIcon = overallStatus.icon;

  return (
    <div className={`flex items-center space-x-2 px-3 py-1 ${overallStatus.bgColor} rounded-full`}>
      <StatusIcon className={`w-4 h-4 ${overallStatus.iconColor}`} />
      <span className={`text-sm font-medium ${overallStatus.textColor}`}>
        {overallStatus.text}
      </span>
    </div>
  );
};

export default StatusIndicator;