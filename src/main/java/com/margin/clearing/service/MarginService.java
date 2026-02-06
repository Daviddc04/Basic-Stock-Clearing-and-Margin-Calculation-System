package com.margin.clearing.service;

import com.margin.clearing.entity.Account;
import com.margin.clearing.entity.Trade;
import com.margin.clearing.repository.AccountRepository;
import com.margin.clearing.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MarginService {
    
    private static final Logger log = LoggerFactory.getLogger(MarginService.class);
    private static final BigDecimal MARGIN_RATE = new BigDecimal("0.10"); // 10% margin requirement
    
    private final AccountRepository accountRepository;
    private final TradeRepository tradeRepository;
    
    public MarginService(AccountRepository accountRepository, TradeRepository tradeRepository) {
        this.accountRepository = accountRepository;
        this.tradeRepository = tradeRepository;
    }
    
    /**
     * Calculates the margin required for a trade
     * Formula: Margin = (Price × Quantity) × 0.10
     */
    public BigDecimal calculateMargin(BigDecimal price, Integer quantity) {
        BigDecimal totalCost = price.multiply(new BigDecimal(quantity));
        return totalCost.multiply(MARGIN_RATE).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Processes a trade with margin validation and atomic account updates
     * Uses pessimistic locking to ensure thread-safe account balance updates
     */
    @Transactional
    public Trade processTrade(String clientId, String symbol, Integer quantity, BigDecimal price) {
        // Calculate margin required
        BigDecimal marginRequired = calculateMargin(price, quantity);
        
        // Create trade with PENDING status
        Trade trade = new Trade();
        trade.setClientId(clientId);
        trade.setSymbol(symbol);
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setMarginRequired(marginRequired);
        trade.setStatus(Trade.TradeStatus.PENDING);
        
        // Lock account for atomic update
        Account account = accountRepository.findByClientIdWithLock(clientId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + clientId));
        
        // Validate margin requirement
        if (account.getBalance().compareTo(marginRequired) < 0) {
            trade.setStatus(Trade.TradeStatus.REJECTED);
            tradeRepository.save(trade);
            log.warn("Trade rejected for client {}: Insufficient margin. Required: {}, Available: {}", 
                    clientId, marginRequired, account.getBalance());
            return trade;
        }
        
        // Deduct margin from account balance
        account.setBalance(account.getBalance().subtract(marginRequired));
        accountRepository.save(account);
        
        // Update trade status to CLEARED
        trade.setStatus(Trade.TradeStatus.CLEARED);
        trade = tradeRepository.save(trade);
        
        log.debug("Trade cleared for client {}: Symbol={}, Quantity={}, Margin={}", 
                clientId, symbol, quantity, marginRequired);
        
        return trade;
    }
}
