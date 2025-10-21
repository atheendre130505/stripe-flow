import { useState } from 'react'
import { Plus, Webhook, Settings } from 'lucide-react'

export function Webhooks() {
  const [searchTerm, setSearchTerm] = useState('')

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Webhooks</h1>
          <p className="text-gray-600">Manage webhook endpoints and delivery logs</p>
        </div>
        <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
          <Plus className="w-4 h-4 mr-2" />
          Add Webhook
        </button>
      </div>

      {/* Webhook Endpoints */}
      <div className="bg-white rounded-lg shadow">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-medium text-gray-900">Webhook Endpoints</h3>
        </div>
        <div className="p-6">
          <div className="text-center py-12">
            <Webhook className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">No webhooks configured</h3>
            <p className="mt-1 text-sm text-gray-500">
              Set up webhook endpoints to receive real-time event notifications.
            </p>
          </div>
        </div>
      </div>

      {/* Delivery Logs */}
      <div className="bg-white rounded-lg shadow">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-medium text-gray-900">Delivery Logs</h3>
        </div>
        <div className="p-6">
          <div className="text-center py-12">
            <Settings className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">No delivery logs</h3>
            <p className="mt-1 text-sm text-gray-500">
              Webhook delivery logs will appear here once endpoints are configured.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

