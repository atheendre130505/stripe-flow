import React from 'react';
import { Settings as SettingsIcon, Key, Shield, Bell, User } from 'lucide-react';
import { Button } from '../../components/UI/Button';

const Settings: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Page header */}
      <div>
        <h1 className="text-2xl font-bold text-secondary-900">Settings</h1>
        <p className="text-secondary-600">Manage your account and application settings</p>
      </div>

      {/* Settings sections */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* API Keys */}
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center mb-4">
            <Key className="w-5 h-5 text-primary-600 mr-3" />
            <h2 className="text-lg font-semibold text-secondary-900">API Keys</h2>
          </div>
          <p className="text-secondary-600 mb-4">Manage your API keys for secure access to the StripeFlow API.</p>
          <Button variant="outline">
            Manage API Keys
          </Button>
        </div>

        {/* Security */}
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center mb-4">
            <Shield className="w-5 h-5 text-primary-600 mr-3" />
            <h2 className="text-lg font-semibold text-secondary-900">Security</h2>
          </div>
          <p className="text-secondary-600 mb-4">Configure security settings and access controls.</p>
          <Button variant="outline">
            Security Settings
          </Button>
        </div>

        {/* Notifications */}
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center mb-4">
            <Bell className="w-5 h-5 text-primary-600 mr-3" />
            <h2 className="text-lg font-semibold text-secondary-900">Notifications</h2>
          </div>
          <p className="text-secondary-600 mb-4">Set up email and webhook notifications for important events.</p>
          <Button variant="outline">
            Notification Settings
          </Button>
        </div>

        {/* Profile */}
        <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
          <div className="flex items-center mb-4">
            <User className="w-5 h-5 text-primary-600 mr-3" />
            <h2 className="text-lg font-semibold text-secondary-900">Profile</h2>
          </div>
          <p className="text-secondary-600 mb-4">Update your personal information and preferences.</p>
          <Button variant="outline">
            Edit Profile
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Settings;
