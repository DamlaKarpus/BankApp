package com.bankapp.bankapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    private String name;

    @Column(unique = true, nullable = false, length = 26) // IBAN alanı
    private String iban;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Account() {}

    public Account(String iban, BigDecimal balance, String name, User user) {
        this.iban = iban;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.name = name;
        this.user = user;
        this.active = true;
    }
}
