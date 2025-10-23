import React from 'react';
import { Filter, X } from 'lucide-react';
import { ChargeFilters } from '../../types';
import { Button } from '../UI/Button';
import { Input } from '../UI/Input';

interface TransactionFiltersProps {
  filters: ChargeFilters;
  onFiltersChange: (filters: ChargeFilters) => void;
}

const TransactionFilters: React.FC<TransactionFiltersProps> = ({
  filters,
  onFiltersChange,
}) => {
  const handleFilterChange = (key: keyof ChargeFilters, value: any) => {
    onFiltersChange({
      ...filters,
      [key]: value,
    });
  };

  const clearFilters = () => {
    onFiltersChange({});
  };

  const hasActiveFilters = Object.values(filters).some(value => 
    value !== undefined && value !== null && value !== ''
  );

  return (
    <div className="flex items-center space-x-4">
      <div className="flex items-center space-x-2">
        <Filter className="w-4 h-4 text-secondary-500" />
        <span className="text-sm text-secondary-700">Filters:</span>
      </div>
      
      <select
        value={filters.status || ''}
        onChange={(e) => handleFilterChange('status', e.target.value || undefined)}
        className="px-3 py-2 border border-secondary-300 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-transparent"
      >
        <option value="">All Status</option>
        <option value="succeeded">Succeeded</option>
        <option value="failed">Failed</option>
        <option value="pending">Pending</option>
        <option value="canceled">Canceled</option>
      </select>

      <Input
        type="date"
        placeholder="From date"
        value={filters.dateFrom || ''}
        onChange={(e) => handleFilterChange('dateFrom', e.target.value || undefined)}
        className="w-40"
      />

      <Input
        type="date"
        placeholder="To date"
        value={filters.dateTo || ''}
        onChange={(e) => handleFilterChange('dateTo', e.target.value || undefined)}
        className="w-40"
      />

      <Input
        type="number"
        placeholder="Min amount"
        value={filters.amountMin || ''}
        onChange={(e) => handleFilterChange('amountMin', e.target.value ? Number(e.target.value) : undefined)}
        className="w-32"
      />

      <Input
        type="number"
        placeholder="Max amount"
        value={filters.amountMax || ''}
        onChange={(e) => handleFilterChange('amountMax', e.target.value ? Number(e.target.value) : undefined)}
        className="w-32"
      />

      {hasActiveFilters && (
        <Button
          variant="ghost"
          size="sm"
          onClick={clearFilters}
        >
          <X className="w-4 h-4 mr-1" />
          Clear
        </Button>
      )}
    </div>
  );
};

export default TransactionFilters;
