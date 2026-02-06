import React, { useState } from 'react';
import './TradeForm.css';

const STOCK_SYMBOLS = ['AAPL', 'GOOGL', 'MSFT', 'AMZN', 'TSLA', 'META', 'NVDA', 'NFLX'];

const TradeForm = ({ accounts, onSubmit, onSuccess }) => {
  const [formData, setFormData] = useState({
    clientId: '',
    symbol: '',
    quantity: '',
    price: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear errors when user starts typing
    if (error) setError(null);
    if (success) setSuccess(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);

    // Validation
    if (!formData.clientId || !formData.symbol || !formData.quantity || !formData.price) {
      setError('All fields are required');
      setLoading(false);
      return;
    }

    const quantity = parseInt(formData.quantity);
    const price = parseFloat(formData.price);

    if (isNaN(quantity) || quantity <= 0) {
      setError('Quantity must be a positive number');
      setLoading(false);
      return;
    }

    if (isNaN(price) || price <= 0) {
      setError('Price must be a positive number');
      setLoading(false);
      return;
    }

    try {
      const trade = await onSubmit({
        clientId: formData.clientId,
        symbol: formData.symbol,
        quantity: quantity,
        price: price
      });

      setSuccess(`Trade ${trade.status}! Margin Required: $${trade.marginRequired.toFixed(2)}`);
      
      // Reset form
      setFormData({
        clientId: formData.clientId, // Keep selected client
        symbol: '',
        quantity: '',
        price: ''
      });

      // Call success callback to refresh trades
      if (onSuccess) {
        onSuccess();
      }
    } catch (err) {
      setError(err.response?.data || err.message || 'Failed to create trade');
    } finally {
      setLoading(false);
    }
  };

  // Calculate margin preview
  const calculateMargin = () => {
    const quantity = parseFloat(formData.quantity);
    const price = parseFloat(formData.price);
    if (quantity > 0 && price > 0) {
      return (quantity * price * 0.10).toFixed(2);
    }
    return '0.00';
  };

  // Get selected account balance
  const selectedAccount = accounts.find(acc => acc.clientId === formData.clientId);
  const accountBalance = selectedAccount ? selectedAccount.balance : null;

  return (
    <div className="trade-form-container">
      <h2>Create New Trade</h2>
      <form onSubmit={handleSubmit} className="trade-form">
        <div className="form-group">
          <label htmlFor="clientId">Client Account *</label>
          <select
            id="clientId"
            name="clientId"
            value={formData.clientId}
            onChange={handleChange}
            required
            disabled={loading}
          >
            <option value="">Select a client account</option>
            {accounts.map(account => (
              <option key={account.id} value={account.clientId}>
                {account.clientId} - Balance: ${account.balance.toFixed(2)}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="symbol">Stock Symbol *</label>
          <select
            id="symbol"
            name="symbol"
            value={formData.symbol}
            onChange={handleChange}
            required
            disabled={loading}
          >
            <option value="">Select a symbol</option>
            {STOCK_SYMBOLS.map(symbol => (
              <option key={symbol} value={symbol}>{symbol}</option>
            ))}
          </select>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="quantity">Quantity *</label>
            <input
              type="number"
              id="quantity"
              name="quantity"
              value={formData.quantity}
              onChange={handleChange}
              min="1"
              step="1"
              required
              disabled={loading}
              placeholder="e.g., 10"
            />
          </div>

          <div className="form-group">
            <label htmlFor="price">Price per Share ($) *</label>
            <input
              type="number"
              id="price"
              name="price"
              value={formData.price}
              onChange={handleChange}
              min="0.01"
              step="0.01"
              required
              disabled={loading}
              placeholder="e.g., 150.00"
            />
          </div>
        </div>

        {(formData.quantity && formData.price) && (
          <div className="margin-preview">
            <strong>Margin Required (10%):</strong> ${calculateMargin()}
            {accountBalance && (
              <span className={`balance-check ${parseFloat(calculateMargin()) > accountBalance ? 'insufficient' : 'sufficient'}`}>
                {parseFloat(calculateMargin()) > accountBalance 
                  ? ` ⚠️ Insufficient balance (Available: $${accountBalance.toFixed(2)})`
                  : ` ✓ Sufficient balance (Available: $${accountBalance.toFixed(2)})`
                }
              </span>
            )}
          </div>
        )}

        {error && (
          <div className="form-error">
            {error}
          </div>
        )}

        {success && (
          <div className="form-success">
            {success}
          </div>
        )}

        <button
          type="submit"
          className="submit-trade-button"
          disabled={loading || !formData.clientId || !formData.symbol || !formData.quantity || !formData.price}
        >
          {loading ? 'Processing Trade...' : 'Submit Trade'}
        </button>
      </form>
    </div>
  );
};

export default TradeForm;
