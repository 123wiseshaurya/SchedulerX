import { useState, useEffect } from 'react';
import { Job, BinaryJob, EmailJob } from '../types';

// Mock data for demonstration
const mockJobs: Job[] = [
  {
    id: '1',
    type: 'BINARY',
    name: 'Data Processing Script',
    status: 'COMPLETED',
    scheduledTime: '2024-12-27T10:00:00',
    createdAt: '2024-12-26T15:30:00',
    lastRun: '2024-12-27T10:00:00',
    repeatPattern: 'DAILY',
    timezone: 'Asia/Kolkata'
  },
  {
    id: '2',
    type: 'EMAIL',
    name: 'Weekly Newsletter',
    status: 'PENDING',
    scheduledTime: '2024-12-28T09:00:00',
    createdAt: '2024-12-26T14:20:00',
    nextRun: '2024-12-28T09:00:00',
    repeatPattern: 'WEEKLY',
    timezone: 'Asia/Kolkata'
  },
  {
    id: '3',
    type: 'BINARY',
    name: 'Backup Script',
    status: 'RUNNING',
    scheduledTime: '2024-12-27T02:00:00',
    createdAt: '2024-12-25T18:45:00',
    repeatPattern: 'DAILY',
    timezone: 'Asia/Kolkata'
  }
];

export const useJobs = () => {
  const [jobs, setJobs] = useState<Job[]>(mockJobs);
  const [loading, setLoading] = useState(false);

  const addJob = (job: Omit<Job, 'id' | 'createdAt' | 'status'>) => {
    const newJob: Job = {
      ...job,
      id: Date.now().toString(),
      createdAt: new Date().toISOString(),
      status: 'PENDING'
    };
    setJobs(prev => [...prev, newJob]);
  };

  const updateJobStatus = (id: string, status: Job['status']) => {
    setJobs(prev => prev.map(job => 
      job.id === id ? { ...job, status } : job
    ));
  };

  const deleteJob = (id: string) => {
    setJobs(prev => prev.filter(job => job.id !== id));
  };

  return {
    jobs,
    loading,
    addJob,
    updateJobStatus,
    deleteJob
  };
};