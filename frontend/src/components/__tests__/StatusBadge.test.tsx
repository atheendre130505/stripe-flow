import React from 'react';
import { render, screen } from '@testing-library/react';
import { StatusBadge } from '../ui/StatusBadge';

describe('StatusBadge', () => {
  it('renders with default props', () => {
    render(<StatusBadge status="PENDING" />);
    const badge = screen.getByText('PENDING');
    expect(badge).toBeInTheDocument();
    expect(badge).toHaveClass('inline-flex', 'items-center', 'px-2.5', 'py-0.5', 'rounded-full', 'text-xs', 'font-medium');
  });

  it('renders success variant for successful status', () => {
    render(<StatusBadge status="SUCCEEDED" />);
    const badge = screen.getByText('SUCCEEDED');
    expect(badge).toHaveClass('bg-green-100', 'text-green-800');
  });

  it('renders warning variant for pending status', () => {
    render(<StatusBadge status="PENDING" />);
    const badge = screen.getByText('PENDING');
    expect(badge).toHaveClass('bg-yellow-100', 'text-yellow-800');
  });

  it('renders error variant for failed status', () => {
    render(<StatusBadge status="FAILED" />);
    const badge = screen.getByText('FAILED');
    expect(badge).toHaveClass('bg-red-100', 'text-red-800');
  });

  it('renders info variant for active status', () => {
    render(<StatusBadge status="ACTIVE" />);
    const badge = screen.getByText('ACTIVE');
    expect(badge).toHaveClass('bg-blue-100', 'text-blue-800');
  });

  it('renders with small size', () => {
    render(<StatusBadge status="PENDING" size="sm" />);
    const badge = screen.getByText('PENDING');
    expect(badge).toHaveClass('px-2', 'py-1', 'text-xs');
  });

  it('renders with large size', () => {
    render(<StatusBadge status="PENDING" size="lg" />);
    const badge = screen.getByText('PENDING');
    expect(badge).toHaveClass('px-3', 'py-1.5', 'text-sm');
  });

  it('renders with custom variant', () => {
    render(<StatusBadge status="CUSTOM" variant="success" />);
    const badge = screen.getByText('CUSTOM');
    expect(badge).toHaveClass('bg-green-100', 'text-green-800');
  });

  it('handles case insensitive status matching', () => {
    render(<StatusBadge status="succeeded" />);
    const badge = screen.getByText('succeeded');
    expect(badge).toHaveClass('bg-green-100', 'text-green-800');
  });

  it('handles status with underscores', () => {
    render(<StatusBadge status="CHARGE_SUCCEEDED" />);
    const badge = screen.getByText('CHARGE_SUCCEEDED');
    expect(badge).toHaveClass('bg-green-100', 'text-green-800');
  });
});


