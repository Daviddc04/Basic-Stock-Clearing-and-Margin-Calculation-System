package com.margin.clearing.service;

import com.margin.clearing.entity.Account;
import com.margin.clearing.entity.Trade;
import com.margin.clearing.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
public class SimulationService {
    
    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);
    private static final String[] STOCK_SYMBOLS = {"AAPL", "GOOGL", "MSFT", "AMZN", "TSLA", "META", "NVDA", "NFLX"};
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal("10000.00");
    private static final int NUM_CLIENTS = 10;
    private static final int NUM_TRADES = 1000;
    
    private final MarginService marginService;
    private final AccountRepository accountRepository;
    private final ExecutorService taskExecutor;
    private final Random random = new Random();
    
    public SimulationService(MarginService marginService, AccountRepository accountRepository, 
                            @Qualifier("taskExecutor") ExecutorService taskExecutor) {
        this.marginService = marginService;
        this.accountRepository = accountRepository;
        this.taskExecutor = taskExecutor;
    }
    
    /**
     * Initializes 10 dummy client accounts with $10,000 each
     */
    public void initializeAccounts() {
        log.info("Initializing {} client accounts with ${} each", NUM_CLIENTS, INITIAL_BALANCE);
        
        for (int i = 1; i <= NUM_CLIENTS; i++) {
            String clientId = "CLIENT_" + String.format("%03d", i);
            Account account = new Account(clientId, INITIAL_BALANCE);
            accountRepository.save(account);
        }
        
        log.info("Successfully initialized {} accounts", NUM_CLIENTS);
    }
    
    /**
     * Generates a random trade request
     */
    private TradeRequest generateRandomTrade() {
        String clientId = "CLIENT_" + String.format("%03d", random.nextInt(NUM_CLIENTS) + 1);
        String symbol = STOCK_SYMBOLS[random.nextInt(STOCK_SYMBOLS.length)];
        int quantity = random.nextInt(100) + 1; // 1-100 shares
        BigDecimal price = BigDecimal.valueOf(random.nextDouble() * 500 + 50) // $50-$550
                .setScale(2, RoundingMode.HALF_UP);
        
        return new TradeRequest(clientId, symbol, quantity, price);
    }
    
    /**
     * Runs the 1,000 trade simulation using CompletableFuture
     * Returns simulation results with timing metrics
     */
    public SimulationResult runSimulation() {
        log.info("Starting simulation of {} trades", NUM_TRADES);
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<CompletableFuture<Trade>> futures = new ArrayList<>();
        
        // Generate 1,000 trade requests
        List<TradeRequest> tradeRequests = IntStream.range(0, NUM_TRADES)
                .mapToObj(i -> generateRandomTrade())
                .toList();
        
        // Process trades asynchronously using CompletableFuture with Spring's TaskExecutor
        for (TradeRequest request : tradeRequests) {
            CompletableFuture<Trade> future = CompletableFuture.supplyAsync(() -> {
                try {
                    Trade trade = marginService.processTrade(
                            request.clientId(),
                            request.symbol(),
                            request.quantity(),
                            request.price()
                    );
                    
                    if (trade.getStatus() == Trade.TradeStatus.CLEARED) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                    
                    return trade;
                } catch (Exception e) {
                    log.error("Error processing trade: {}", e.getMessage());
                    failureCount.incrementAndGet();
                    return null;
                }
            }, taskExecutor);
            
            futures.add(future);
        }
        
        // Wait for all trades to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        stopWatch.stop();
        
        long totalTimeMs = stopWatch.getTotalTimeMillis();
        double averageTimeMs = (double) totalTimeMs / NUM_TRADES;
        
        SimulationResult result = new SimulationResult(
                NUM_TRADES,
                successCount.get(),
                failureCount.get(),
                totalTimeMs,
                averageTimeMs
        );
        
        log.info("Simulation completed: {} trades processed in {}ms (avg: {}ms/trade)", 
                NUM_TRADES, totalTimeMs, String.format("%.2f", averageTimeMs));
        log.info("Success: {}, Failed: {}", successCount.get(), failureCount.get());
        
        return result;
    }
    
    /**
     * Trade request record
     */
    public record TradeRequest(
            String clientId,
            String symbol,
            Integer quantity,
            BigDecimal price
    ) {}
    
    /**
     * Simulation result record
     */
    public record SimulationResult(
            int totalTrades,
            int successCount,
            int failureCount,
            long totalTimeMs,
            double averageTimeMs
    ) {}
}
