import React, { useState } from 'react';
import Header from './components/Header';
import TabNavigation from './components/TabNavigation';
import BinaryJobForm from './components/BinaryJobForm';
import EmailJobForm from './components/EmailJobForm';
import EmailTestPanel from './components/EmailTestPanel';
import JobsList from './components/JobsList';
import { useJobs } from './hooks/useJobs';
import { BinaryFormData, EmailFormData } from './types';

function App() {
  const [activeTab, setActiveTab] = useState<'binary' | 'email' | 'test'>('binary');
  const { jobs, addJob, updateJobStatus, deleteJob } = useJobs();

  const handleBinaryJobSubmit = (data: BinaryFormData) => {
    if (!data.file || !data.presignedUrl) {
      alert('Please upload a binary file first');
      return;
    }

    addJob({
      type: 'BINARY',
      name: data.name,
      scheduledTime: data.scheduledTime,
      repeatPattern: data.repeatPattern,
      timezone: data.timezone
    });

    // Show success message
    alert('Binary job scheduled successfully!');
  };

  const handleEmailJobSubmit = (data: EmailFormData) => {
    if (data.recipients.length === 0) {
      alert('Please add at least one recipient');
      return;
    }

    addJob({
      type: 'EMAIL',
      name: data.name,
      scheduledTime: data.scheduledTime,
      repeatPattern: data.repeatPattern,
      timezone: data.timezone
    });

    // Show success message
    alert('Email campaign scheduled successfully!');
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="space-y-8">
          {/* Job Creation Section */}
          <div className="bg-white rounded-lg shadow-sm border border-gray-200">
            <div className="border-b border-gray-200">
              <nav className="-mb-px flex space-x-8">
                <button
                  onClick={() => setActiveTab('binary')}
                  className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors duration-200 ${
                    activeTab === 'binary'
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  <div className="flex items-center space-x-2">
                    <span>Binary Jobs</span>
                  </div>
                </button>
                
                <button
                  onClick={() => setActiveTab('email')}
                  className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors duration-200 ${
                    activeTab === 'email'
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  <div className="flex items-center space-x-2">
                    <span>Email Jobs</span>
                  </div>
                </button>

                <button
                  onClick={() => setActiveTab('test')}
                  className={`py-4 px-1 border-b-2 font-medium text-sm transition-colors duration-200 ${
                    activeTab === 'test'
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  <div className="flex items-center space-x-2">
                    <span>Email Test</span>
                  </div>
                </button>
              </nav>
            </div>
            
            <div className="p-6">
              {activeTab === 'binary' && (
                <BinaryJobForm onSubmit={handleBinaryJobSubmit} />
              )}
              {activeTab === 'email' && (
                <EmailJobForm onSubmit={handleEmailJobSubmit} />
              )}
              {activeTab === 'test' && (
                <EmailTestPanel />
              )}
            </div>
          </div>

          {/* Statistics Section */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-8 h-8 bg-blue-100 rounded-md flex items-center justify-center">
                    <span className="text-blue-600 font-semibold text-sm">
                      {jobs.filter(job => job.status === 'PENDING').length}
                    </span>
                  </div>
                </div>
                <div className="ml-4">
                  <h3 className="text-sm font-medium text-gray-900">Pending Jobs</h3>
                  <p className="text-xs text-gray-500">Scheduled for execution</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-8 h-8 bg-green-100 rounded-md flex items-center justify-center">
                    <span className="text-green-600 font-semibold text-sm">
                      {jobs.filter(job => job.status === 'COMPLETED').length}
                    </span>
                  </div>
                </div>
                <div className="ml-4">
                  <h3 className="text-sm font-medium text-gray-900">Completed</h3>
                  <p className="text-xs text-gray-500">Successfully executed</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-8 h-8 bg-yellow-100 rounded-md flex items-center justify-center">
                    <span className="text-yellow-600 font-semibold text-sm">
                      {jobs.filter(job => job.status === 'RUNNING').length}
                    </span>
                  </div>
                </div>
                <div className="ml-4">
                  <h3 className="text-sm font-medium text-gray-900">Running</h3>
                  <p className="text-xs text-gray-500">Currently executing</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="w-8 h-8 bg-red-100 rounded-md flex items-center justify-center">
                    <span className="text-red-600 font-semibold text-sm">
                      {jobs.filter(job => job.status === 'FAILED').length}
                    </span>
                  </div>
                </div>
                <div className="ml-4">
                  <h3 className="text-sm font-medium text-gray-900">Failed</h3>
                  <p className="text-xs text-gray-500">Execution failed</p>
                </div>
              </div>
            </div>
          </div>

          {/* Jobs List Section */}
          <JobsList 
            jobs={jobs}
            onUpdateStatus={updateJobStatus}
            onDeleteJob={deleteJob}
          />
        </div>
      </main>
    </div>
  );
}

export default App;