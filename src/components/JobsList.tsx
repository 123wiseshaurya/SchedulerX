import React from 'react';
import { Play, Pause, Trash2, Calendar, Clock, Users, FileCode, Mail, MoreHorizontal, CheckCircle, XCircle, Loader } from 'lucide-react';
import { Job } from '../types';

interface JobsListProps {
  jobs: Job[];
  onUpdateStatus: (id: string, status: Job['status']) => void;
  onDeleteJob: (id: string) => void;
}

const JobsList: React.FC<JobsListProps> = ({ jobs, onUpdateStatus, onDeleteJob }) => {
  const getStatusIcon = (status: Job['status']) => {
    switch (status) {
      case 'COMPLETED':
        return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'FAILED':
        return <XCircle className="w-4 h-4 text-red-500" />;
      case 'RUNNING':
        return <Loader className="w-4 h-4 text-blue-500 animate-spin" />;
      case 'CANCELLED':
        return <XCircle className="w-4 h-4 text-gray-500" />;
      default:
        return <Clock className="w-4 h-4 text-yellow-500" />;
    }
  };

  const getStatusColor = (status: Job['status']) => {
    switch (status) {
      case 'COMPLETED':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'FAILED':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'RUNNING':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'CANCELLED':
        return 'bg-gray-100 text-gray-800 border-gray-200';
      default:
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    }
  };

  const formatDateTime = (dateString: string) => {
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      timeZoneName: 'short'
    });
  };

  const getTypeIcon = (type: Job['type']) => {
    return type === 'BINARY' ? 
      <FileCode className="w-4 h-4 text-blue-600" /> : 
      <Mail className="w-4 h-4 text-green-600" />;
  };

  const getTypeColor = (type: Job['type']) => {
    return type === 'BINARY' ? 
      'bg-blue-50 text-blue-700 border-blue-200' : 
      'bg-green-50 text-green-700 border-green-200';
  };

  if (jobs.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
        <Calendar className="w-12 h-12 text-gray-400 mx-auto mb-4" />
        <h3 className="text-lg font-medium text-gray-900 mb-2">No jobs scheduled</h3>
        <p className="text-gray-500">Create your first job using the forms above.</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200">
      <div className="p-6 border-b border-gray-200">
        <h2 className="text-lg font-semibold text-gray-900">Scheduled Jobs</h2>
        <p className="text-sm text-gray-500 mt-1">Manage your scheduled tasks and campaigns</p>
      </div>
      
      <div className="divide-y divide-gray-200">
        {jobs.map((job) => (
          <div key={job.id} className="p-6 hover:bg-gray-50 transition-colors duration-150">
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <div className="flex items-center space-x-3 mb-3">
                  <div className={`inline-flex items-center space-x-1 px-2 py-1 rounded-full text-xs font-medium border ${getTypeColor(job.type)}`}>
                    {getTypeIcon(job.type)}
                    <span>{job.type.toLowerCase()}</span>
                  </div>
                  
                  <div className={`inline-flex items-center space-x-1 px-2 py-1 rounded-full text-xs font-medium border ${getStatusColor(job.status)}`}>
                    {getStatusIcon(job.status)}
                    <span>{job.status.toLowerCase()}</span>
                  </div>
                  
                  <div className="inline-flex items-center space-x-1 px-2 py-1 rounded-full text-xs font-medium bg-purple-50 text-purple-700 border border-purple-200">
                    <Calendar className="w-3 h-3" />
                    <span>{job.repeatPattern.toLowerCase()}</span>
                  </div>
                </div>
                
                <h3 className="text-lg font-medium text-gray-900 mb-2">{job.name}</h3>
                
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm text-gray-600">
                  <div className="flex items-center space-x-2">
                    <Clock className="w-4 h-4" />
                    <div>
                      <span className="font-medium">Scheduled:</span>
                      <p className="text-gray-800">{formatDateTime(job.scheduledTime)}</p>
                    </div>
                  </div>
                  
                  {job.lastRun && (
                    <div className="flex items-center space-x-2">
                      <CheckCircle className="w-4 h-4" />
                      <div>
                        <span className="font-medium">Last Run:</span>
                        <p className="text-gray-800">{formatDateTime(job.lastRun)}</p>
                      </div>
                    </div>
                  )}
                  
                  {job.nextRun && (
                    <div className="flex items-center space-x-2">
                      <Calendar className="w-4 h-4" />
                      <div>
                        <span className="font-medium">Next Run:</span>
                        <p className="text-gray-800">{formatDateTime(job.nextRun)}</p>
                      </div>
                    </div>
                  )}
                </div>
                
                {job.type === 'EMAIL' && (
                  <div className="mt-3 flex items-center space-x-2 text-sm text-gray-600">
                    <Users className="w-4 h-4" />
                    <span>Recipients: Multiple recipients configured</span>
                  </div>
                )}
              </div>
              
              <div className="flex items-center space-x-2 ml-4">
                {job.status === 'PENDING' && (
                  <button
                    onClick={() => onUpdateStatus(job.id, 'RUNNING')}
                    className="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-blue-700 bg-blue-100 hover:bg-blue-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200"
                  >
                    <Play className="w-3 h-3 mr-1" />
                    Run Now
                  </button>
                )}
                
                {job.status === 'RUNNING' && (
                  <button
                    onClick={() => onUpdateStatus(job.id, 'CANCELLED')}
                    className="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-orange-700 bg-orange-100 hover:bg-orange-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500 transition-colors duration-200"
                  >
                    <Pause className="w-3 h-3 mr-1" />
                    Cancel
                  </button>
                )}
                
                <button
                  onClick={() => onDeleteJob(job.id)}
                  className="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-red-700 bg-red-100 hover:bg-red-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 transition-colors duration-200"
                >
                  <Trash2 className="w-3 h-3 mr-1" />
                  Delete
                </button>
                
                <button className="inline-flex items-center px-2 py-1.5 border border-gray-300 text-xs font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors duration-200">
                  <MoreHorizontal className="w-3 h-3" />
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default JobsList;