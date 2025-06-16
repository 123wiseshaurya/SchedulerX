export interface Job {
  id: string;
  type: 'BINARY' | 'EMAIL';
  name: string;
  status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  scheduledTime: string;
  createdAt: string;
  lastRun?: string;
  nextRun?: string;
  repeatPattern: RepeatPattern;
  timezone: string;
}

export interface BinaryJob extends Job {
  type: 'BINARY';
  filePath: string;
  fileSize: number;
  presignedUrl?: string;
  delayMinutes: number;
}

export interface EmailJob extends Job {
  type: 'EMAIL';
  recipients: string[];
  subject: string;
  content: string;
  template?: string;
  attachments?: string[];
}

export type RepeatPattern = 'ONCE' | 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY' | 'CUSTOM';

export interface ScheduleFormData {
  name: string;
  scheduledTime: string;
  timezone: string;
  repeatPattern: RepeatPattern;
  delayMinutes: number;
}

export interface BinaryFormData extends ScheduleFormData {
  file: File | null;
  presignedUrl: string;
}

export interface EmailFormData extends ScheduleFormData {
  recipients: string[];
  subject: string;
  content: string;
  template: string;
}