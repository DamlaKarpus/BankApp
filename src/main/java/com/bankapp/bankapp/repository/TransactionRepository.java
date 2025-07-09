package com.bankapp.bankapp.repository;

import com.bankapp.bankapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrTargetAccountId(Long accountId, Long targetAccountId);
}
