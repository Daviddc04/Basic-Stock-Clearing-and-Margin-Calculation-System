package com.margin.clearing.repository;

import com.margin.clearing.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    
    @Query(value = "SELECT * FROM trades ORDER BY created_at DESC LIMIT 20", nativeQuery = true)
    List<Trade> findLast20Trades();
    
    List<Trade> findByClientIdOrderByCreatedAtDesc(String clientId);
}
