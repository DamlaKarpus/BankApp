package com.bankapp.bankapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // İşlemi yapan hesap
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Havale ise hedef hesap
    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    // İşlem yapılan hesap IBAN numarası (string)
    private String accountIban;

    // Tutar
    private Double amount;

    // İşlem zamanı
    @CreationTimestamp
    private LocalDateTime transactionTime;

    // İşlem tipi (TRANSFER, DEPOSIT, WITHDRAW)
    private String type;
}
