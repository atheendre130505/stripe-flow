import { useState } from 'react'
import { Plus, Key, Eye, EyeOff, Copy } from 'lucide-react'

export function ApiKeys() {
  const [searchTerm, setSearchTerm] = useState('')
  const [showKeys, setShowKeys] = useState(false)

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">API Keys</h1>
          <p className="text-gray-600">Manage API keys for authentication</p>
        </div>
        <button className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center">
          <Plus className="w-4 h-4 mr-2" />
          Generate Key
        </button>
      </div>

      {/* API Keys List */}
      <div className="bg-white rounded-lg shadow">
        <div className="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
          <h3 className="text-lg font-medium text-gray-900">API Keys</h3>
          <button
            onClick={() => setShowKeys(!showKeys)}
            className="flex items-center text-sm text-gray-600 hover:text-gray-900"
          >
            {showKeys ? <EyeOff className="w-4 h-4 mr-1" /> : <Eye className="w-4 h-4 mr-1" />}
            {showKeys ? 'Hide' : 'Show'} Keys
          </button>
        </div>
        <div className="p-6">
          <div className="text-center py-12">
            <Key className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">No API keys generated</h3>
            <p className="mt-1 text-sm text-gray-500">
              Generate your first API key to start making requests to the StripeFlow API.
            </p>
          </div>
        </div>
      </div>

      {/* API Documentation */}
      <div className="bg-white rounded-lg shadow">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-medium text-gray-900">API Documentation</h3>
        </div>
        <div className="p-6">
          <div className="space-y-4">
            <div>
              <h4 className="text-sm font-medium text-gray-900">Base URL</h4>
              <code className="text-sm text-gray-600 bg-gray-100 px-2 py-1 rounded">
                http://localhost:8080/api/v1
              </code>
            </div>
            <div>
              <h4 className="text-sm font-medium text-gray-900">Authentication</h4>
              <p className="text-sm text-gray-600">
                Include your API key in the X-API-Key header for all requests.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

