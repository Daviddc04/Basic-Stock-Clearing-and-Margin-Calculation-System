import React from 'react';
import './SimulationMetrics.css';

const SimulationMetrics = ({ result }) => {
  if (!result) return null;

  const successRate = ((result.successCount / result.totalTrades) * 100).toFixed(2);
  const failureRate = ((result.failureCount / result.totalTrades) * 100).toFixed(2);

  return (
    <div className="metrics-container">
      <h2>Simulation Results</h2>
      <div className="metrics-grid">
        <div className="metric-card">
          <div className="metric-label">Total Time Elapsed</div>
          <div className="metric-value highlight">
            {result.totalTimeMs}ms
          </div>
        </div>
        <div className="metric-card">
          <div className="metric-label">Average Response Time</div>
          <div className="metric-value highlight">
            {result.averageTimeMs.toFixed(2)}ms/trade
          </div>
        </div>
        <div className="metric-card">
          <div className="metric-label">Total Trades</div>
          <div className="metric-value">{result.totalTrades}</div>
        </div>
        <div className="metric-card success">
          <div className="metric-label">Success Rate</div>
          <div className="metric-value">
            {result.successCount} ({successRate}%)
          </div>
        </div>
        <div className="metric-card failure">
          <div className="metric-label">Failure Rate</div>
          <div className="metric-value">
            {result.failureCount} ({failureRate}%)
          </div>
        </div>
      </div>
      {result.totalTimeMs < 350 && (
        <div className="performance-badge">
          ✓ Sub-350ms Performance Target Achieved!
        </div>
      )}
    </div>
  );
};

export default SimulationMetrics;
