import React from 'react';
import { Calendar, Mail, FileText, Zap } from 'lucide-react';
import StatusIndicator from './StatusIndicator';

const Header: React.FC = () => {
  return (
    <header className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center space-x-4">
            <div className="flex items-center justify-center w-10 h-10 bg-gradient-to-r from-blue-600 to-purple-600 rounded-lg">
              <Calendar className="w-6 h-6 text-white" />
            </div>
            <div>
              <h1 className="text-xl font-bold text-gray-900">JobScheduler Pro</h1>
              <p className="text-sm text-gray-500">Enterprise scheduling platform</p>
            </div>
          </div>
          
          <div className="flex items-center space-x-6">
            <div className="hidden md:flex items-center space-x-6">
              <div className="flex items-center space-x-2 text-sm text-gray-600">
                <FileText className="w-4 h-4 text-blue-500" />
                <span>Binary Scripts</span>
              </div>
              <div className="flex items-center space-x-2 text-sm text-gray-600">
                <Mail className="w-4 h-4 text-green-500" />
                <span>Email Campaigns</span>
              </div>
              <div className="flex items-center space-x-2 text-sm text-gray-600">
                <Zap className="w-4 h-4 text-yellow-500" />
                <span>Kafka Integration</span>
              </div>
            </div>
            
            <StatusIndicator />
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;