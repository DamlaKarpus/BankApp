package com.bankapp.bankapp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionBaseModel {

    private String accountIban;
    private String targetAccountIban; // Havale/transfer için
    private BigDecimal amount;
    private String type; // DEPOSIT, WITHDRAW, TRANSFER
    private LocalDateTime transactionTime;

    public String getAccountIban() {
        return accountIban;
    }

    public void setAccountIban(String accountIban) {
        this.accountIban = accountIban;
    }

    public String getTargetAccountIban() {
        return targetAccountIban;
    }

    public void setTargetAccountIban(String targetAccountIban) {
        this.targetAccountIban = targetAccountIban;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
}
