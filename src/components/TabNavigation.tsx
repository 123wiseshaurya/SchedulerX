import React from 'react';
import { FileCode, Mail } from 'lucide-react';

interface TabNavigationProps {
  activeTab: 'binary' | 'email';
  onTabChange: (tab: 'binary' | 'email') => void;
}

const TabNavigation: React.FC<TabNavigationProps> = ({ activeTab, onTabChange }) => {
  return (
    <div className="border-b border-gray-200">
      <nav className="-mb-px flex space-x-8">
        <button
          onClick={() => onTabChange('binary')}
          className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors duration-200 ${
            activeTab === 'binary'
              ? 'border-blue-500 text-blue-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
          }`}
        >
          <div className="flex items-center space-x-2">
            <FileCode className="w-5 h-5" />
            <span>Binary Jobs</span>
          </div>
        </button>
        
        <button
          onClick={() => onTabChange('email')}
          className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors duration-200 ${
            activeTab === 'email'
              ? 'border-blue-500 text-blue-600'
              : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
          }`}
        >
          <div className="flex items-center space-x-2">
            <Mail className="w-5 h-5" />
            <span>Email Jobs</span>
          </div>
        </button>
      </nav>
    </div>
  );
};

export default TabNavigation;