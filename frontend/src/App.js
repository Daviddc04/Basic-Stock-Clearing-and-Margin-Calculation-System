import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import TradeTable from './components/TradeTable';
import SimulationMetrics from './components/SimulationMetrics';
import TradeForm from './components/TradeForm';

const API_BASE_URL = 'http://localhost:8080/api';

function App() {
  const [trades, setTrades] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [simulationResult, setSimulationResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchTrades();
    fetchAccounts();
    initializeAccounts();
  }, []);

  const initializeAccounts = async () => {
    try {
      await axios.post(`${API_BASE_URL}/accounts/initialize`);
      // Refresh accounts after initialization
      setTimeout(() => {
        fetchAccounts();
      }, 500);
    } catch (err) {
      console.error('Failed to initialize accounts:', err);
    }
  };

  const fetchAccounts = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/accounts`);
      setAccounts(response.data);
    } catch (err) {
      console.error('Failed to fetch accounts:', err);
    }
  };

  const fetchTrades = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/trades`);
      setTrades(response.data);
    } catch (err) {
      setError('Failed to fetch trades');
      console.error(err);
    }
  };

  const createTrade = async (tradeData) => {
    const response = await axios.post(`${API_BASE_URL}/trades`, tradeData);
    return response.data;
  };

  const runSimulation = async () => {
    setLoading(true);
    setError(null);
    setSimulationResult(null);

    try {
      const response = await axios.post(`${API_BASE_URL}/trades/simulate`);
      setSimulationResult(response.data);
      
      // Refresh trades after simulation
      setTimeout(() => {
        fetchTrades();
      }, 500);
    } catch (err) {
      setError('Simulation failed: ' + (err.response?.data?.message || err.message));
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Margin Calculation & Stock Clearing Dashboard</h1>
      </header>

      <main className="App-main">
        <TradeForm 
          accounts={accounts} 
          onSubmit={createTrade}
          onSuccess={() => {
            fetchTrades();
            fetchAccounts(); // Refresh account balances
          }}
        />

        <div className="controls-section">
          <button
            className="simulate-button"
            onClick={runSimulation}
            disabled={loading}
          >
            {loading ? 'Running Simulation...' : 'Run 1,000 Trade Simulation'}
          </button>
        </div>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {simulationResult && (
          <SimulationMetrics result={simulationResult} />
        )}

        <div className="trades-section">
          <h2>Last 20 Trades</h2>
          <TradeTable trades={trades} />
        </div>
      </main>
    </div>
  );
}

export default App;
