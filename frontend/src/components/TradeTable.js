import React from 'react';
import './TradeTable.css';

const TradeTable = ({ trades }) => {
  const getStatusColor = (status) => {
    switch (status) {
      case 'CLEARED':
        return '#4CAF50'; // Green
      case 'REJECTED':
        return '#f44336'; // Red
      case 'PENDING':
        return '#ff9800'; // Orange
      default:
        return '#757575'; // Gray
    }
  };

  const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return 'N/A';
    const date = new Date(dateTimeString);
    return date.toLocaleString();
  };

  if (!trades || trades.length === 0) {
    return (
      <div className="no-trades">
        <p>No trades available. Run a simulation to see trades.</p>
      </div>
    );
  }

  return (
    <div className="trade-table-container">
      <table className="trade-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Client ID</th>
            <th>Symbol</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Margin Required</th>
            <th>Status</th>
            <th>Created At</th>
          </tr>
        </thead>
        <tbody>
          {trades.map((trade) => (
            <tr key={trade.id}>
              <td>{trade.id}</td>
              <td>{trade.clientId}</td>
              <td className="symbol-cell">{trade.symbol}</td>
              <td>{trade.quantity}</td>
              <td>${trade.price.toFixed(2)}</td>
              <td>${trade.marginRequired.toFixed(2)}</td>
              <td>
                <span
                  className="status-badge"
                  style={{ backgroundColor: getStatusColor(trade.status) }}
                >
                  {trade.status}
                </span>
              </td>
              <td className="date-cell">{formatDateTime(trade.createdAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TradeTable;
