package com.margin.clearing.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades", indexes = @Index(name = "idx_client_id", columnList = "clientId"))
public class Trade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String clientId;
    
    @Column(nullable = false)
    private String symbol;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal marginRequired;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Trade() {
    }
    
    public Trade(Long id, String clientId, String symbol, Integer quantity, BigDecimal price, 
                 BigDecimal marginRequired, TradeStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.clientId = clientId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.marginRequired = marginRequired;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getMarginRequired() {
        return marginRequired;
    }
    
    public void setMarginRequired(BigDecimal marginRequired) {
        this.marginRequired = marginRequired;
    }
    
    public TradeStatus getStatus() {
        return status;
    }
    
    public void setStatus(TradeStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public enum TradeStatus {
        PENDING,
        CLEARED,
        REJECTED
    }
}
