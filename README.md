# Margin Calculation and Stock Clearing System

MVP implementation for a margin calculation and stock clearing system that demonstrates:
- **1,000+ Trade Simulation** capability
- **Sub-350ms Response Time** performance
- **Margin Validation** with atomic account updates
- **RESTful APIs** for trade and account management
- **React Dashboard** for visualization

## Architecture

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2.0
- **Database**: H2 In-Memory (optimized for performance testing)
- **Concurrency**: CompletableFuture with ThreadPool (50-100 threads)
- **Persistence**: JPA with batch insert optimization
- **Locking**: Pessimistic locking for thread-safe account updates

### Frontend (React)
- **Framework**: React 18.2.0
- **HTTP Client**: Axios
- **Styling**: CSS with modern UI design
- **Components**: TradeTable, SimulationMetrics

## Key Features

### 1. Simulation Engine
- Generates 1,000 random trade requests
- Uses CompletableFuture for parallel processing
- Tracks success/failure rates
- Measures total and average response times

### 2. Margin Calculation
- Formula: `Margin = (Price × Quantity) × 0.10` (10% requirement)
- Validates account balance before trade execution
- Atomic updates using pessimistic locking

### 3. Performance Optimization
- H2 in-memory database for speed
- Batch insert configuration (batch_size=50)
- Async processing with thread pool
- Indexed database queries

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- Node.js 16+ and npm

### Backend Setup

1. Navigate to project root:
```bash
cd MarginCalculationandStockClearing
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## API Endpoints

### Trades
- `GET /api/trades` - Get last 20 trades
- `GET /api/trades/client/{clientId}` - Get trades by client ID
- `POST /api/trades/simulate` - Run 1,000 trade simulation

### Accounts
- `GET /api/accounts` - Get all accounts
- `GET /api/accounts/{clientId}` - Get account by client ID
- `POST /api/accounts/initialize` - Initialize 10 dummy accounts

## Testing

### Backend Tests (JUnit 5)
```bash
mvn test
```

Tests include:
- **Happy Path**: User has enough money for trade
- **Edge Case**: User has exactly $0 balance
- **Simulation**: 1,000 trade processing verification
- **Account Balance**: Mathematical correctness validation

### Frontend Tests (Jest)
```bash
cd frontend
npm test
```

Tests include:
- **TradeTable**: Renders rows correctly when passed JSON array

## Performance Metrics

The system is designed to achieve:
- **Sub-350ms** total execution time for 1,000 trades
- **<0.35ms** average response time per trade
- **Thread-safe** account balance updates
- **Mathematically correct** final account balances

## Project Structure

```
MarginCalculationandStockClearing/
├── src/
│   ├── main/
│   │   ├── java/com/margin/clearing/
│   │   │   ├── entity/          # Trade, Account entities
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── service/         # MarginService, SimulationService
│   │   │   ├── controller/      # REST controllers
│   │   │   └── config/          # Async, CORS configuration
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # JUnit tests
├── frontend/
│   ├── src/
│   │   ├── components/          # React components
│   │   └── App.js
│   └── package.json
└── pom.xml
```

## License

This project is created for demonstration purposes.
