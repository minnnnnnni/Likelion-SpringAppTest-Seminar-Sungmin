package com.likelion.banking.domain;

import java.math.BigDecimal;

/**
 * 계좌 도메인 모델
 */
public class Account {
    
    private Long id;
    private String ownerName;
    private BigDecimal amount;
    
    public Account() {
    }
    
    public Account(Long id, String ownerName, BigDecimal amount) {
        this.id = id;
        this.ownerName = ownerName;
        this.amount = amount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}