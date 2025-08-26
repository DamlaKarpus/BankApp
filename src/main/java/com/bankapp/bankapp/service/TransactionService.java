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
    @Transactional
    public Transaction deposit(String iban, double amount) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı"));

        account.setBalance(account.getBalance().add(java.math.BigDecimal.valueOf(amount)));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setType("DEPOSIT");
        transaction.setAccountIban(account.getIban());

        return transactionRepository.save(transaction);
    }

    // Para çekme
    @Transactional
    public Transaction withdraw(String iban, double amount) {
        Account account = accountRepository.findByIban(iban)
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
        transaction.setAccountIban(account.getIban());

        return transactionRepository.save(transaction);
    }

    // Para gönderme (havale)
    @Transactional
    public Transaction transfer(String senderIban, String receiverIban, double amount) {
        Account sender = accountRepository.findByIban(senderIban)
                .orElseThrow(() -> new RuntimeException("Gönderen hesap bulunamadı"));

        Account receiver = accountRepository.findByIban(receiverIban)
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
        transaction.setAccountIban(sender.getIban());

        return transactionRepository.save(transaction);
    }

    // İşlem geçmişi (hem gönderen hem alıcı işlemleri)
    public List<Transaction> getTransactionHistory(String iban) {
        return transactionRepository.findByAccountIbanOrTargetAccountIban(iban, iban);
    }
}
