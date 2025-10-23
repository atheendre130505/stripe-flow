import React from 'react';

interface StatusBadgeProps {
  status: string;
  variant?: 'default' | 'success' | 'warning' | 'error' | 'info';
  size?: 'sm' | 'md' | 'lg';
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ 
  status, 
  variant = 'default',
  size = 'md' 
}) => {
  const getVariantClasses = () => {
    switch (variant) {
      case 'success':
        return 'bg-green-100 text-green-800';
      case 'warning':
        return 'bg-yellow-100 text-yellow-800';
      case 'error':
        return 'bg-red-100 text-red-800';
      case 'info':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getSizeClasses = () => {
    switch (size) {
      case 'sm':
        return 'px-2 py-1 text-xs';
      case 'lg':
        return 'px-3 py-1.5 text-sm';
      default:
        return 'px-2.5 py-0.5 text-xs';
    }
  };

  const getStatusVariant = (status: string) => {
    const statusLower = status.toLowerCase();
    
    if (statusLower.includes('success') || statusLower.includes('succeeded') || statusLower.includes('delivered')) {
      return 'success';
    }
    if (statusLower.includes('pending') || statusLower.includes('processing') || statusLower.includes('retrying')) {
      return 'warning';
    }
    if (statusLower.includes('failed') || statusLower.includes('error') || statusLower.includes('canceled')) {
      return 'error';
    }
    if (statusLower.includes('active') || statusLower.includes('enabled')) {
      return 'success';
    }
    if (statusLower.includes('inactive') || statusLower.includes('disabled')) {
      return 'error';
    }
    
    return 'default';
  };

  const statusVariant = getStatusVariant(status);

  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getVariantClasses()} ${getSizeClasses()}`}
    >
      {status}
    </span>
  );
};


