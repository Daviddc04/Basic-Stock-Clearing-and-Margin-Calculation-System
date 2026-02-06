import React from 'react';
import { render, screen } from '@testing-library/react';
import TradeTable from '../TradeTable';

describe('TradeTable', () => {
  const mockTrades = [
    {
      id: 1,
      clientId: 'CLIENT_001',
      symbol: 'AAPL',
      quantity: 10,
      price: 150.50,
      marginRequired: 150.50,
      status: 'CLEARED',
      createdAt: '2024-01-01T10:00:00'
    },
    {
      id: 2,
      clientId: 'CLIENT_002',
      symbol: 'GOOGL',
      quantity: 5,
      price: 200.00,
      marginRequired: 100.00,
      status: 'REJECTED',
      createdAt: '2024-01-01T11:00:00'
    },
    {
      id: 3,
      clientId: 'CLIENT_003',
      symbol: 'MSFT',
      quantity: 20,
      price: 300.75,
      marginRequired: 601.50,
      status: 'PENDING',
      createdAt: '2024-01-01T12:00:00'
    }
  ];

  it('renders trade table with rows correctly when passed a JSON array', () => {
    render(<TradeTable trades={mockTrades} />);

    // Check if table headers are rendered
    expect(screen.getByText('ID')).toBeInTheDocument();
    expect(screen.getByText('Client ID')).toBeInTheDocument();
    expect(screen.getByText('Symbol')).toBeInTheDocument();
    expect(screen.getByText('Status')).toBeInTheDocument();

    // Check if trade data is rendered
    expect(screen.getByText('1')).toBeInTheDocument();
    expect(screen.getByText('CLIENT_001')).toBeInTheDocument();
    expect(screen.getByText('AAPL')).toBeInTheDocument();
    expect(screen.getByText('CLEARED')).toBeInTheDocument();

    expect(screen.getByText('2')).toBeInTheDocument();
    expect(screen.getByText('CLIENT_002')).toBeInTheDocument();
    expect(screen.getByText('GOOGL')).toBeInTheDocument();
    expect(screen.getByText('REJECTED')).toBeInTheDocument();

    expect(screen.getByText('3')).toBeInTheDocument();
    expect(screen.getByText('CLIENT_003')).toBeInTheDocument();
    expect(screen.getByText('MSFT')).toBeInTheDocument();
    expect(screen.getByText('PENDING')).toBeInTheDocument();
  });

  it('displays correct number of rows', () => {
    render(<TradeTable trades={mockTrades} />);
    
    // Get all table rows (excluding header)
    const rows = screen.getAllByRole('row');
    // Should have 1 header row + 3 data rows = 4 total
    expect(rows.length).toBe(4);
  });

  it('displays empty state when no trades provided', () => {
    render(<TradeTable trades={[]} />);
    expect(screen.getByText(/No trades available/i)).toBeInTheDocument();
  });

  it('displays empty state when trades is null', () => {
    render(<TradeTable trades={null} />);
    expect(screen.getByText(/No trades available/i)).toBeInTheDocument();
  });
});
