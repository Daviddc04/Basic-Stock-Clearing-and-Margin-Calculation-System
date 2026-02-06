package com.margin.clearing.service;

import com.margin.clearing.entity.Account;
import com.margin.clearing.entity.Trade;
import com.margin.clearing.repository.AccountRepository;
import com.margin.clearing.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SimulationServiceTest {

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @BeforeEach
    void setUp() {
        // Clear existing data
        tradeRepository.deleteAll();
        accountRepository.deleteAll();
    }

    /**
     * Test that simulation runs successfully and processes 1,000 trades
     */
    @Test
    void testRunSimulation_Processes1000Trades() {
        // Arrange - initialize accounts first
        simulationService.initializeAccounts();
        
        // Act
        SimulationService.SimulationResult result = simulationService.runSimulation();

        // Assert
        assertNotNull(result);
        assertEquals(1000, result.totalTrades());
        assertTrue(result.totalTrades() > 0);
        assertEquals(result.totalTrades(), result.successCount() + result.failureCount());
        assertTrue(result.totalTimeMs() > 0);
        assertTrue(result.averageTimeMs() > 0);

        // Verify trades were created in database
        List<Trade> allTrades = tradeRepository.findAll();
        assertEquals(1000, allTrades.size());
    }

    /**
     * Test that accounts are initialized correctly
     */
    @Test
    void testInitializeAccounts_Creates10Accounts() {
        // Arrange - ensure accounts are deleted (already done in setUp)
        // Act
        simulationService.initializeAccounts();

        // Assert
        List<Account> accounts = accountRepository.findAll();
        assertEquals(10, accounts.size());

        // Verify each account has correct balance
        for (Account account : accounts) {
            assertEquals(new BigDecimal("10000.00"), account.getBalance());
            assertTrue(account.getClientId().startsWith("CLIENT_"));
        }
    }

    /**
     * Test that simulation completes without crashing
     */
    @Test
    void testRunSimulation_CompletesWithoutCrashing() {
        // Arrange - initialize accounts first
        simulationService.initializeAccounts();
        
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> {
            SimulationService.SimulationResult result = simulationService.runSimulation();
            assertNotNull(result);
        });
    }

    /**
     * Test that final account balances are mathematically correct
     */
    @Test
    void testRunSimulation_AccountBalancesAreCorrect() {
        // Arrange - initialize accounts first
        simulationService.initializeAccounts();
        
        // Act
        SimulationService.SimulationResult result = simulationService.runSimulation();

        // Assert - verify account balances
        List<Account> accounts = accountRepository.findAll();
        
        // Calculate expected total balance
        BigDecimal initialTotalBalance = new BigDecimal("100000.00"); // 10 accounts * $10,000
        
        // Calculate actual total balance
        BigDecimal actualTotalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate total margin deducted from cleared trades
        List<Trade> clearedTrades = tradeRepository.findAll().stream()
                .filter(t -> t.getStatus() == Trade.TradeStatus.CLEARED)
                .toList();
        
        BigDecimal totalMarginDeducted = clearedTrades.stream()
                .map(Trade::getMarginRequired)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Expected balance = Initial - Margin Deducted
        BigDecimal expectedTotalBalance = initialTotalBalance.subtract(totalMarginDeducted);
        
        // Verify balances match (within rounding tolerance)
        assertEquals(0, expectedTotalBalance.compareTo(actualTotalBalance), 
                "Account balances should be mathematically correct");
        
        assertEquals(result.successCount(), clearedTrades.size());
    }
}
