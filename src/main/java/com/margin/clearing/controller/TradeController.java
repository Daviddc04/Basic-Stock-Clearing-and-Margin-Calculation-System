package com.margin.clearing.controller;

import com.margin.clearing.dto.TradeRequestDTO;
import com.margin.clearing.entity.Trade;
import com.margin.clearing.repository.TradeRepository;
import com.margin.clearing.service.MarginService;
import com.margin.clearing.service.SimulationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trades")
@CrossOrigin(origins = "http://localhost:3000")
public class TradeController {
    
    private final TradeRepository tradeRepository;
    private final SimulationService simulationService;
    private final MarginService marginService;
    
    public TradeController(TradeRepository tradeRepository, SimulationService simulationService, MarginService marginService) {
        this.tradeRepository = tradeRepository;
        this.simulationService = simulationService;
        this.marginService = marginService;
    }
    
    /**
     * Get the last 20 trades
     */
    @GetMapping
    public ResponseEntity<List<Trade>> getLast20Trades() {
        List<Trade> trades = tradeRepository.findLast20Trades();
        return ResponseEntity.ok(trades);
    }
    
    /**
     * Get trades by client ID
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Trade>> getTradesByClient(@PathVariable String clientId) {
        List<Trade> trades = tradeRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        return ResponseEntity.ok(trades);
    }
    
    /**
     * Create a new trade (manual trade creation)
     */
    @PostMapping
    public ResponseEntity<?> createTrade(@RequestBody TradeRequestDTO tradeRequest) {
        try {
            Trade trade = marginService.processTrade(
                    tradeRequest.getClientId(),
                    tradeRequest.getSymbol(),
                    tradeRequest.getQuantity(),
                    tradeRequest.getPrice()
            );
            return ResponseEntity.ok(trade);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing trade: " + e.getMessage());
        }
    }
    
    /**
     * Run the 1,000 trade simulation
     */
    @PostMapping("/simulate")
    public ResponseEntity<SimulationService.SimulationResult> runSimulation() {
        SimulationService.SimulationResult result = simulationService.runSimulation();
        return ResponseEntity.ok(result);
    }
}
