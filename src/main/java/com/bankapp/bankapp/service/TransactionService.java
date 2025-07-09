package com.bankapp.bankapp.service;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.entity.Transaction;
import com.bankapp.bankapp.repository.AccountRepository;
import com.bankapp.bankapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // Para yatırma
    public Transaction deposit(Long accountId, double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı"));

        account.setBalance(account.getBalance().add(java.math.BigDecimal.valueOf(amount)));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setType("DEPOSIT");
        transaction.setAccountNumber(account.getId().toString());

        return transactionRepository.save(transaction);
    }

    // Para çekme
    public Transaction withdraw(Long accountId, double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı"));

        if (account.getBalance().compareTo(java.math.BigDecimal.valueOf(amount)) < 0) {
            throw new RuntimeException("Yetersiz bakiye");
        }

        account.setBalance(account.getBalance().subtract(java.math.BigDecimal.valueOf(amount)));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setType("WITHDRAW");
        transaction.setAccountNumber(account.getId().toString());

        return transactionRepository.save(transaction);
    }

    // Para gönderme (havale)
    @Transactional
    public Transaction transfer(Long senderId, Long receiverId, double amount) {
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Gönderen hesap bulunamadı"));

        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Alıcı hesap bulunamadı"));

        if (sender.getBalance().compareTo(java.math.BigDecimal.valueOf(amount)) < 0) {
            throw new RuntimeException("Yetersiz bakiye");
        }

        sender.setBalance(sender.getBalance().subtract(java.math.BigDecimal.valueOf(amount)));
        receiver.setBalance(receiver.getBalance().add(java.math.BigDecimal.valueOf(amount)));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction transaction = new Transaction();
        transaction.setAccount(sender);
        transaction.setTargetAccount(receiver);
        transaction.setAmount(amount);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setType("TRANSFER");
        transaction.setAccountNumber(sender.getId().toString());

        return transactionRepository.save(transaction);
    }

    // İşlem geçmişi (hem gönderen hem alıcı işlemleri)
    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountIdOrTargetAccountId(accountId, accountId);
    }
}
