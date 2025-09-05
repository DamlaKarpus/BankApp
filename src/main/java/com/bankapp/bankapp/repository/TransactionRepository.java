package com.bankapp.bankapp.repository;

import com.bankapp.bankapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // IBAN üzerinden işlem geçmişini çekmek için (son yapılan işlem en üstte)
    List<Transaction> findByAccountIbanOrTargetAccountIbanOrderByTransactionTimeDesc(
            String accountIban,
            String targetAccountIban
    );
}
