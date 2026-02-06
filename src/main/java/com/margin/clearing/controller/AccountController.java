package com.margin.clearing.controller;

import com.margin.clearing.entity.Account;
import com.margin.clearing.repository.AccountRepository;
import com.margin.clearing.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {
    
    private final AccountRepository accountRepository;
    private final SimulationService simulationService;
    
    public AccountController(AccountRepository accountRepository, SimulationService simulationService) {
        this.accountRepository = accountRepository;
        this.simulationService = simulationService;
    }
    
    /**
     * Get all accounts
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return ResponseEntity.ok(accounts);
    }
    
    /**
     * Get account by client ID
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<Account> getAccount(@PathVariable String clientId) {
        return accountRepository.findByClientId(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Initialize dummy accounts for simulation
     */
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeAccounts() {
        simulationService.initializeAccounts();
        return ResponseEntity.ok("Accounts initialized successfully");
    }
}
