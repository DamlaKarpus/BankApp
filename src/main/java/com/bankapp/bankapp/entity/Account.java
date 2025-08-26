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

    private Boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private BigDecimal balance = BigDecimal.ZERO;

    private String name;

    @Column(unique = true, nullable = false, length = 26) // IBAN alanı
    private String iban;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
