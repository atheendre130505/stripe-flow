import React from 'react';
import { Users, Plus, Search, Download } from 'lucide-react';
import { Button } from '../../components/UI/Button';
import { Input } from '../../components/UI/Input';

const Customers: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Page header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-secondary-900">Customers</h1>
          <p className="text-secondary-600">Manage your customer database</p>
        </div>
        <div className="flex items-center space-x-3">
          <Button variant="outline">
            <Download className="w-4 h-4 mr-2" />
            Export
          </Button>
          <Button>
            <Plus className="w-4 h-4 mr-2" />
            Add Customer
          </Button>
        </div>
      </div>

      {/* Search and filters */}
      <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
        <div className="flex items-center space-x-4">
          <div className="flex-1 max-w-md">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-secondary-400" />
              <Input
                placeholder="Search customers..."
                className="pl-10"
              />
            </div>
          </div>
          <select className="px-3 py-2 border border-secondary-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-transparent">
            <option>All customers</option>
            <option>Active</option>
            <option>Inactive</option>
          </select>
        </div>
      </div>

      {/* Customers table placeholder */}
      <div className="bg-white rounded-lg shadow-sm border border-secondary-200">
        <div className="p-12 text-center">
          <Users className="w-12 h-12 mx-auto mb-4 text-secondary-300" />
          <h3 className="text-lg font-medium text-secondary-900 mb-2">No customers found</h3>
          <p className="text-secondary-500">Get started by adding your first customer.</p>
        </div>
      </div>
    </div>
  );
};

export default Customers;
