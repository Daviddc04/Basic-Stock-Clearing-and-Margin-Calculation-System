package com.margin.clearing.service;

import com.margin.clearing.entity.Account;
import com.margin.clearing.entity.Trade;
import com.margin.clearing.repository.AccountRepository;
import com.margin.clearing.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MarginServiceTest {

    @Autowired
    private MarginService marginService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TradeRepository tradeRepository;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        // Create a test account with sufficient balance
        testAccount = new Account("TEST_CLIENT", new BigDecimal("10000.00"));
        accountRepository.save(testAccount);
    }

    /**
     * Happy Path Test: User has enough money
     * This test verifies that a trade is successfully cleared when the account has sufficient balance
     */
    @Test
    void testProcessTrade_HappyPath_UserHasEnoughMoney() {
        // Arrange
        String clientId = "TEST_CLIENT";
        String symbol = "AAPL";
        Integer quantity = 10;
        BigDecimal price = new BigDecimal("150.00");
        
        // Expected margin: (150.00 * 10) * 0.10 = 150.00
        BigDecimal expectedMargin = new BigDecimal("150.00");

        // Act
        Trade result = marginService.processTrade(clientId, symbol, quantity, price);

        // Assert
        assertNotNull(result);
        assertEquals(Trade.TradeStatus.CLEARED, result.getStatus());
        assertEquals(clientId, result.getClientId());
        assertEquals(symbol, result.getSymbol());
        assertEquals(quantity, result.getQuantity());
        assertEquals(price, result.getPrice());
        assertEquals(expectedMargin, result.getMarginRequired());

        // Verify account balance was deducted
        Account updatedAccount = accountRepository.findByClientId(clientId).orElseThrow();
        BigDecimal expectedBalance = new BigDecimal("10000.00").subtract(expectedMargin);
        assertEquals(expectedBalance, updatedAccount.getBalance());
    }

    /**
     * Edge Case Test: User has exactly $0
     * This test verifies that a trade is rejected when the account has zero balance
     */
    @Test
    void testProcessTrade_EdgeCase_UserHasExactlyZero() {
        // Arrange
        String clientId = "TEST_CLIENT";
        
        // Set account balance to exactly $0
        testAccount.setBalance(BigDecimal.ZERO);
        accountRepository.save(testAccount);

        String symbol = "AAPL";
        Integer quantity = 10;
        BigDecimal price = new BigDecimal("150.00");
        
        // Expected margin: (150.00 * 10) * 0.10 = 150.00
        BigDecimal expectedMargin = new BigDecimal("150.00");

        // Act
        Trade result = marginService.processTrade(clientId, symbol, quantity, price);

        // Assert
        assertNotNull(result);
        assertEquals(Trade.TradeStatus.REJECTED, result.getStatus());
        assertEquals(expectedMargin, result.getMarginRequired());

        // Verify account balance was NOT deducted (still $0)
        Account updatedAccount = accountRepository.findByClientId(clientId).orElseThrow();
        assertEquals(BigDecimal.ZERO, updatedAccount.getBalance());
    }

    /**
     * Edge Case Test: User has insufficient balance (less than required margin)
     */
    @Test
    void testProcessTrade_EdgeCase_InsufficientBalance() {
        // Arrange
        String clientId = "TEST_CLIENT";
        
        // Set account balance to $50 (less than required margin)
        testAccount.setBalance(new BigDecimal("50.00"));
        accountRepository.save(testAccount);

        String symbol = "AAPL";
        Integer quantity = 10;
        BigDecimal price = new BigDecimal("150.00");
        
        // Expected margin: (150.00 * 10) * 0.10 = 150.00
        BigDecimal expectedMargin = new BigDecimal("150.00");

        // Act
        Trade result = marginService.processTrade(clientId, symbol, quantity, price);

        // Assert
        assertNotNull(result);
        assertEquals(Trade.TradeStatus.REJECTED, result.getStatus());
        assertEquals(expectedMargin, result.getMarginRequired());

        // Verify account balance was NOT deducted
        Account updatedAccount = accountRepository.findByClientId(clientId).orElseThrow();
        assertEquals(new BigDecimal("50.00"), updatedAccount.getBalance());
    }

    /**
     * Test margin calculation formula
     */
    @Test
    void testCalculateMargin() {
        // Arrange
        BigDecimal price = new BigDecimal("100.00");
        Integer quantity = 50;
        
        // Expected: (100.00 * 50) * 0.10 = 500.00
        BigDecimal expectedMargin = new BigDecimal("500.00");

        // Act
        BigDecimal result = marginService.calculateMargin(price, quantity);

        // Assert
        assertEquals(expectedMargin, result);
    }

    /**
     * Test that account not found throws exception
     */
    @Test
    void testProcessTrade_AccountNotFound() {
        // Arrange
        String nonExistentClientId = "NON_EXISTENT_CLIENT";
        String symbol = "AAPL";
        Integer quantity = 10;
        BigDecimal price = new BigDecimal("150.00");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            marginService.processTrade(nonExistentClientId, symbol, quantity, price);
        });
    }
}
