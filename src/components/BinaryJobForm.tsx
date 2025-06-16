import React, { useState } from 'react';
import { Upload, Clock, Repeat, MapPin, Rocket } from 'lucide-react';
import { BinaryFormData, RepeatPattern } from '../types';

interface BinaryJobFormProps {
  onSubmit: (data: BinaryFormData) => void;
}

const BinaryJobForm: React.FC<BinaryJobFormProps> = ({ onSubmit }) => {
  const [formData, setFormData] = useState<BinaryFormData>({
    name: '',
    file: null,
    presignedUrl: '',
    scheduledTime: '',
    delayMinutes: 0,
    repeatPattern: 'ONCE',
    timezone: 'Asia/Kolkata'
  });

  const [uploading, setUploading] = useState(false);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setFormData(prev => ({
      ...prev,
      file,
      name: file ? file.name.replace(/\.[^/.]+$/, '') : prev.name
    }));
  };

  const handleUpload = async () => {
    if (!formData.file) return;
    
    setUploading(true);
    // Simulate upload process
    await new Promise(resolve => setTimeout(resolve, 2000));
    setFormData(prev => ({
      ...prev,
      presignedUrl: `https://storage.example.com/binaries/${prev.file?.name}`
    }));
    setUploading(false);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center space-x-3 mb-6">
        <div className="flex items-center justify-center w-10 h-10 bg-blue-100 rounded-lg">
          <Upload className="w-5 h-5 text-blue-600" />
        </div>
        <div>
          <h2 className="text-lg font-semibold text-gray-900">Schedule Binary Job</h2>
          <p className="text-sm text-gray-500">Upload and schedule script execution</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Job Name
          </label>
          <input
            type="text"
            value={formData.name}
            onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="Enter job name"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Binary File
          </label>
          <div className="space-y-3">
            <input
              type="file"
              onChange={handleFileChange}
              className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-medium file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
              accept=".exe,.sh,.py,.jar,.bin"
            />
            {formData.file && (
              <div className="flex items-center justify-between p-3 bg-gray-50 rounded-md">
                <span className="text-sm text-gray-600">
                  {formData.file.name} ({(formData.file.size / 1024 / 1024).toFixed(2)} MB)
                </span>
                <button
                  type="button"
                  onClick={handleUpload}
                  disabled={uploading}
                  className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center space-x-2"
                >
                  {uploading ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                      <span>Uploading...</span>
                    </>
                  ) : (
                    <>
                      <Upload className="w-4 h-4" />
                      <span>Upload Binary</span>
                    </>
                  )}
                </button>
              </div>
            )}
            {formData.presignedUrl && (
              <div className="p-3 bg-green-50 border border-green-200 rounded-md">
                <p className="text-sm text-green-800">
                  ✓ File uploaded successfully
                </p>
                <p className="text-xs text-green-600 mt-1 truncate">
                  {formData.presignedUrl}
                </p>
              </div>
            )}
          </div>
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
          className="w-full bg-blue-600 text-white py-3 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 font-medium flex items-center justify-center space-x-2 transition-colors duration-200"
        >
          <Rocket className="w-5 h-5" />
          <span>Schedule Job</span>
        </button>
      </form>
    </div>
  );
};

export default BinaryJobForm;