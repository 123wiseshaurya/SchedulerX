import React, { useState } from 'react';
import { Send, Clock, Repeat, MapPin, Users, Mail } from 'lucide-react';
import { EmailFormData, RepeatPattern } from '../types';

interface EmailJobFormProps {
  onSubmit: (data: EmailFormData) => void;
}

const EmailJobForm: React.FC<EmailJobFormProps> = ({ onSubmit }) => {
  const [formData, setFormData] = useState<EmailFormData>({
    name: '',
    recipients: [''],
    subject: '',
    content: '',
    template: 'default',
    scheduledTime: '',
    delayMinutes: 0,
    repeatPattern: 'ONCE',
    timezone: 'Asia/Kolkata'
  });

  const addRecipient = () => {
    setFormData(prev => ({
      ...prev,
      recipients: [...prev.recipients, '']
    }));
  };

  const updateRecipient = (index: number, value: string) => {
    setFormData(prev => ({
      ...prev,
      recipients: prev.recipients.map((recipient, i) => i === index ? value : recipient)
    }));
  };

  const removeRecipient = (index: number) => {
    setFormData(prev => ({
      ...prev,
      recipients: prev.recipients.filter((_, i) => i !== index)
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const validRecipients = formData.recipients.filter(email => email.trim() !== '');
    onSubmit({
      ...formData,
      recipients: validRecipients
    });
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center space-x-3 mb-6">
        <div className="flex items-center justify-center w-10 h-10 bg-green-100 rounded-lg">
          <Mail className="w-5 h-5 text-green-600" />
        </div>
        <div>
          <h2 className="text-lg font-semibold text-gray-900">Schedule Email Job</h2>
          <p className="text-sm text-gray-500">Create and schedule email campaigns</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Campaign Name
          </label>
          <input
            type="text"
            value={formData.name}
            onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Enter campaign name"
            required
          />
        </div>

        <div>
          <label className="flex items-center space-x-2 text-sm font-medium text-gray-700 mb-2">
            <Users className="w-4 h-4" />
            <span>Recipients</span>
          </label>
          <div className="space-y-2">
            {formData.recipients.map((recipient, index) => (
              <div key={index} className="flex items-center space-x-2">
                <input
                  type="email"
                  value={recipient}
                  onChange={(e) => updateRecipient(index, e.target.value)}
                  className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Enter email address"
                />
                {formData.recipients.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeRecipient(index)}
                    className="px-3 py-2 text-red-600 hover:text-red-800 font-medium"
                  >
                    Remove
                  </button>
                )}
              </div>
            ))}
            <button
              type="button"
              onClick={addRecipient}
              className="text-blue-600 hover:text-blue-800 font-medium text-sm"
            >
              + Add Recipient
            </button>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Email Template
          </label>
          <select
            value={formData.template}
            onChange={(e) => setFormData(prev => ({ ...prev, template: e.target.value }))}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="default">Default Template</option>
            <option value="newsletter">Newsletter Template</option>
            <option value="promotional">Promotional Template</option>
            <option value="notification">Notification Template</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Subject
          </label>
          <input
            type="text"
            value={formData.subject}
            onChange={(e) => setFormData(prev => ({ ...prev, subject: e.target.value }))}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Enter email subject"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Email Content
          </label>
          <textarea
            value={formData.content}
            onChange={(e) => setFormData(prev => ({ ...prev, content: e.target.value }))}
            rows={8}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Enter your email content here..."
            required
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="flex items-center space-x-2 text-sm font-medium text-gray-700 mb-2">
              <Clock className="w-4 h-4" />
              <span>Scheduled Time</span>
            </label>
            <input
              type="datetime-local"
              value={formData.scheduledTime}
              onChange={(e) => setFormData(prev => ({ ...prev, scheduledTime: e.target.value }))}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Delay (minutes)
            </label>
            <input
              type="number"
              value={formData.delayMinutes}
              onChange={(e) => setFormData(prev => ({ ...prev, delayMinutes: parseInt(e.target.value) || 0 }))}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              min="0"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="flex items-center space-x-2 text-sm font-medium text-gray-700 mb-2">
              <Repeat className="w-4 h-4" />
              <span>Repeat</span>
            </label>
            <select
              value={formData.repeatPattern}
              onChange={(e) => setFormData(prev => ({ ...prev, repeatPattern: e.target.value as RepeatPattern }))}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ONCE">Once</option>
              <option value="DAILY">Daily</option>
              <option value="WEEKLY">Weekly</option>
              <option value="MONTHLY">Monthly</option>
              <option value="YEARLY">Yearly</option>
            </select>
          </div>

          <div>
            <label className="flex items-center space-x-2 text-sm font-medium text-gray-700 mb-2">
              <MapPin className="w-4 h-4" />
              <span>Time Zone</span>
            </label>
            <select
              value={formData.timezone}
              onChange={(e) => setFormData(prev => ({ ...prev, timezone: e.target.value }))}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="Asia/Kolkata">Asia/Kolkata</option>
              <option value="UTC">UTC</option>
              <option value="America/New_York">America/New_York</option>
              <option value="Europe/London">Europe/London</option>
              <option value="Asia/Tokyo">Asia/Tokyo</option>
            </select>
          </div>
        </div>

        <button
          type="submit"
          className="w-full bg-green-600 text-white py-3 px-4 rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 font-medium flex items-center justify-center space-x-2 transition-colors duration-200"
        >
          <Send className="w-5 h-5" />
          <span>Schedule Email</span>
        </button>
      </form>
    </div>
  );
};

export default EmailJobForm;