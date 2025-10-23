import React from 'react';
import { Webhook, Plus, Settings, Activity } from 'lucide-react';
import { Button } from '../../components/UI/Button';

const Webhooks: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Page header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-secondary-900">Webhooks</h1>
          <p className="text-secondary-600">Manage webhook endpoints and events</p>
        </div>
        <Button>
          <Plus className="w-4 h-4 mr-2" />
          Add Webhook
        </Button>
      </div>

      {/* Webhook endpoints */}
      <div className="bg-white rounded-lg shadow-sm border border-secondary-200">
        <div className="p-6 border-b border-secondary-200">
          <h2 className="text-lg font-semibold text-secondary-900">Webhook Endpoints</h2>
        </div>
        <div className="p-6">
          <div className="text-center py-12">
            <Webhook className="w-12 h-12 mx-auto mb-4 text-secondary-300" />
            <h3 className="text-lg font-medium text-secondary-900 mb-2">No webhooks configured</h3>
            <p className="text-secondary-500 mb-4">Set up webhooks to receive real-time notifications about events.</p>
            <Button>
              <Plus className="w-4 h-4 mr-2" />
              Create Webhook
            </Button>
          </div>
        </div>
      </div>

      {/* Webhook events */}
      <div className="bg-white rounded-lg shadow-sm border border-secondary-200">
        <div className="p-6 border-b border-secondary-200">
          <h2 className="text-lg font-semibold text-secondary-900">Recent Events</h2>
        </div>
        <div className="p-6">
          <div className="text-center py-12">
            <Activity className="w-12 h-12 mx-auto mb-4 text-secondary-300" />
            <h3 className="text-lg font-medium text-secondary-900 mb-2">No events yet</h3>
            <p className="text-secondary-500">Webhook events will appear here once you have active endpoints.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Webhooks;
