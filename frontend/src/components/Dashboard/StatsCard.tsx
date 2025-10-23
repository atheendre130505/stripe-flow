import React from 'react';
import { LucideIcon } from 'lucide-react';
import { cn } from '../../utils';

interface StatsCardProps {
  title: string;
  value: string;
  change: string;
  changeType: 'positive' | 'negative' | 'neutral';
  icon: LucideIcon;
  color: 'primary' | 'secondary' | 'success' | 'warning' | 'error';
  loading?: boolean;
}

const StatsCard: React.FC<StatsCardProps> = ({
  title,
  value,
  change,
  changeType,
  icon: Icon,
  color,
  loading = false,
}) => {
  const colorClasses = {
    primary: 'bg-primary-50 text-primary-600',
    secondary: 'bg-secondary-50 text-secondary-600',
    success: 'bg-success-50 text-success-600',
    warning: 'bg-warning-50 text-warning-600',
    error: 'bg-error-50 text-error-600',
  };

  const changeIconClasses = {
    positive: 'text-success-600',
    negative: 'text-error-600',
    neutral: 'text-secondary-600',
  };

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
        <div className="animate-pulse">
          <div className="flex items-center justify-between mb-4">
            <div className="h-4 bg-secondary-200 rounded w-24"></div>
            <div className="h-8 w-8 bg-secondary-200 rounded"></div>
          </div>
          <div className="h-8 bg-secondary-200 rounded w-20 mb-2"></div>
          <div className="h-4 bg-secondary-200 rounded w-16"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-secondary-200 p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-secondary-600">{title}</h3>
        <div className={cn('p-2 rounded-lg', colorClasses[color])}>
          <Icon className="w-5 h-5" />
        </div>
      </div>
      
      <div className="space-y-2">
        <div className="text-2xl font-bold text-secondary-900">{value}</div>
        <div className="flex items-center text-sm">
          <span className={cn('font-medium', changeIconClasses[changeType])}>
            {change}
          </span>
          <span className="text-secondary-500 ml-1">vs last period</span>
        </div>
      </div>
    </div>
  );
};

export default StatsCard;
