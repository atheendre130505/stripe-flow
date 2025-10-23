import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { Plus, Search, Filter, Download, RefreshCw } from 'lucide-react';
import { chargeService } from '../../services/chargeService';
import { ChargeFilters } from '../../types';
import TransactionTable from '../../components/Transactions/TransactionTable';
import TransactionFilters from '../../components/Transactions/TransactionFilters';
import CreateTransactionModal from '../../components/Transactions/CreateTransactionModal';
import { Button } from '../../components/UI/Button';
import { Input } from '../../components/UI/Input';

const Transactions: React.FC = () => {
  const [filters, setFilters] = useState<ChargeFilters>({});
  const [searchQuery, setSearchQuery] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);

  const { data, isLoading, refetch } = useQuery(
    ['transactions', currentPage, pageSize, filters, searchQuery],
    () => {
      if (searchQuery) {
        return chargeService.searchCharges(searchQuery, currentPage, pageSize);
      }
      return chargeService.getCharges(currentPage, pageSize, filters);
    },
    { keepPreviousData: true }
  );

  const handleFilterChange = (newFilters: ChargeFilters) => {
    setFilters(newFilters);
    setCurrentPage(1);
  };

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    setCurrentPage(1);
  };

  const handleExport = async () => {
    try {
      const blob = await chargeService.exportCharges(filters);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `transactions-${new Date().toISOString().split('T')[0]}.csv`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Export failed:', error);
    }
  };

  return (
    <div className="space-y-6">
      {/* Page header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-secondary-900">Transactions</h1>
          <p className="text-secondary-600">Manage and monitor all payment transactions</p>
        </div>
        <div className="flex items-center space-x-3">
          <Button
            variant="outline"
            onClick={() => refetch()}
            disabled={isLoading}
          >
            <RefreshCw className={`w-4 h-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
            Refresh
          </Button>
          <Button
            variant="outline"
            onClick={handleExport}
          >
            <Download className="w-4 h-4 mr-2" />
            Export
          </Button>
          <Button
            onClick={() => setShowCreateModal(true)}
          >
            <Plus className="w-4 h-4 mr-2" />
            New Transaction
          </Button>
        </div>
      </div>

      {/* Filters and search */}
      <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between space-y-4 lg:space-y-0">
          <div className="flex-1 max-w-md">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-secondary-400" />
              <Input
                placeholder="Search transactions..."
                value={searchQuery}
                onChange={(e) => handleSearch(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
          <TransactionFilters
            filters={filters}
            onFiltersChange={handleFilterChange}
          />
        </div>
      </div>

      {/* Transactions table */}
      <div className="bg-white rounded-lg shadow-sm border border-secondary-200">
        <TransactionTable
          data={data?.data || []}
          loading={isLoading}
          pagination={data?.pagination}
          onPageChange={setCurrentPage}
          onPageSizeChange={setPageSize}
        />
      </div>

      {/* Create transaction modal */}
      {showCreateModal && (
        <CreateTransactionModal
          isOpen={showCreateModal}
          onClose={() => setShowCreateModal(false)}
          onSuccess={() => {
            setShowCreateModal(false);
            refetch();
          }}
        />
      )}
    </div>
  );
};

export default Transactions;
