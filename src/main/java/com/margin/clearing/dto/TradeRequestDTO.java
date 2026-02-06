package com.margin.clearing.dto;

import java.math.BigDecimal;

public class TradeRequestDTO {
    private String clientId;
    private String symbol;
    private Integer quantity;
    private BigDecimal price;

    public TradeRequestDTO() {
    }

    public TradeRequestDTO(String clientId, String symbol, Integer quantity, BigDecimal price) {
        this.clientId = clientId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
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
}
