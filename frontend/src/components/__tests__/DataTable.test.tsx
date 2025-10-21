import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { DataTable } from '../ui/DataTable';

interface TestItem {
  id: number;
  name: string;
  status: string;
  amount: number;
}

const testData: TestItem[] = [
  { id: 1, name: 'Item 1', status: 'ACTIVE', amount: 100 },
  { id: 2, name: 'Item 2', status: 'PENDING', amount: 200 },
  { id: 3, name: 'Item 3', status: 'FAILED', amount: 300 },
];

const testColumns = [
  {
    key: 'id' as keyof TestItem,
    title: 'ID',
    render: (value: number) => <span data-testid="id-cell">#{value}</span>
  },
  {
    key: 'name' as keyof TestItem,
    title: 'Name',
    render: (name: string) => <span data-testid="name-cell">{name}</span>
  },
  {
    key: 'status' as keyof TestItem,
    title: 'Status',
    render: (status: string) => <span data-testid="status-cell">{status}</span>
  },
  {
    key: 'amount' as keyof TestItem,
    title: 'Amount',
    render: (amount: number) => <span data-testid="amount-cell">${amount}</span>
  }
];

describe('DataTable', () => {
  it('renders data correctly', () => {
    render(<DataTable data={testData} columns={testColumns} />);
    
    expect(screen.getByText('ID')).toBeInTheDocument();
    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Status')).toBeInTheDocument();
    expect(screen.getByText('Amount')).toBeInTheDocument();
    
    expect(screen.getAllByTestId('id-cell')).toHaveLength(3);
    expect(screen.getAllByTestId('name-cell')).toHaveLength(3);
    expect(screen.getAllByTestId('status-cell')).toHaveLength(3);
    expect(screen.getAllByTestId('amount-cell')).toHaveLength(3);
  });

  it('renders loading state', () => {
    render(<DataTable data={[]} columns={testColumns} loading={true} />);
    
    expect(screen.getByRole('generic')).toHaveClass('animate-pulse');
  });

  it('renders empty state', () => {
    render(<DataTable data={[]} columns={testColumns} />);
    
    expect(screen.getByText('No data available')).toBeInTheDocument();
  });

  it('renders custom empty message', () => {
    render(<DataTable data={[]} columns={testColumns} emptyMessage="No items found" />);
    
    expect(screen.getByText('No items found')).toBeInTheDocument();
  });

  it('handles row click', () => {
    const onRowClick = jest.fn();
    render(<DataTable data={testData} columns={testColumns} onRowClick={onRowClick} />);
    
    const firstRow = screen.getAllByRole('row')[1]; // Skip header row
    fireEvent.click(firstRow);
    
    expect(onRowClick).toHaveBeenCalledWith(testData[0]);
  });

  it('applies custom className', () => {
    render(<DataTable data={testData} columns={testColumns} className="custom-class" />);
    
    const table = screen.getByRole('table');
    expect(table.closest('.custom-class')).toBeInTheDocument();
  });

  it('renders table headers correctly', () => {
    render(<DataTable data={testData} columns={testColumns} />);
    
    const headers = screen.getAllByRole('columnheader');
    expect(headers).toHaveLength(4);
    expect(headers[0]).toHaveTextContent('ID');
    expect(headers[1]).toHaveTextContent('Name');
    expect(headers[2]).toHaveTextContent('Status');
    expect(headers[3]).toHaveTextContent('Amount');
  });

  it('renders table rows correctly', () => {
    render(<DataTable data={testData} columns={testColumns} />);
    
    const rows = screen.getAllByRole('row');
    expect(rows).toHaveLength(4); // 1 header + 3 data rows
    
    // Check first data row
    const firstDataRow = rows[1];
    expect(firstDataRow).toHaveTextContent('#1');
    expect(firstDataRow).toHaveTextContent('Item 1');
    expect(firstDataRow).toHaveTextContent('ACTIVE');
    expect(firstDataRow).toHaveTextContent('$100');
  });

  it('applies hover styles when onRowClick is provided', () => {
    const onRowClick = jest.fn();
    render(<DataTable data={testData} columns={testColumns} onRowClick={onRowClick} />);
    
    const firstRow = screen.getAllByRole('row')[1];
    expect(firstRow).toHaveClass('cursor-pointer');
  });

  it('does not apply hover styles when onRowClick is not provided', () => {
    render(<DataTable data={testData} columns={testColumns} />);
    
    const firstRow = screen.getAllByRole('row')[1];
    expect(firstRow).not.toHaveClass('cursor-pointer');
  });
});
