package com.bankapp.bankapp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionBaseModel {

    private String accountIban;          // İşlemi yapanın IBAN'ı
    private String targetAccountIban;    // Transferlerde alıcının IBAN'ı
    private BigDecimal amount;           // İşlem tutarı
    private String type;                 // DEPOSIT, WITHDRAW, TRANSFER
    private LocalDateTime transactionTime; // İşlem zamanı
    private String targetUserName;       // Karşı tarafın kullanıcı adı

    // --- Getters & Setters ---
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

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }
}
