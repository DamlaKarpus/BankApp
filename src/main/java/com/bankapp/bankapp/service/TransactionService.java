package com.bankapp.bankapp.service;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.entity.Transaction;
import com.bankapp.bankapp.entity.TransactionBaseModel;
import com.bankapp.bankapp.repository.AccountRepository;
import com.bankapp.bankapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public TransactionBaseModel deposit(String iban, BigDecimal amount) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setType("DEPOSIT");
        transaction.setAccountIban(account.getIban());

        transactionRepository.save(transaction);

        TransactionBaseModel dto = new TransactionBaseModel();
        dto.setAccountIban(account.getIban());
        dto.setAmount(amount);
        dto.setType("DEPOSIT");
        dto.setTransactionTime(transaction.getTransactionTime());

        return dto;
    }

    // Para çekme
    @Transactional
    public TransactionBaseModel withdraw(String iban, BigDecimal amount) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz bakiye");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setType("WITHDRAW");
        transaction.setAccountIban(account.getIban());

        transactionRepository.save(transaction);

        TransactionBaseModel dto = new TransactionBaseModel();
        dto.setAccountIban(account.getIban());
        dto.setAmount(amount);
        dto.setType("WITHDRAW");
        dto.setTransactionTime(transaction.getTransactionTime());

        return dto;
    }

    // Para gönderme (havale)
    @Transactional
    public TransactionBaseModel transfer(String senderIban, String receiverIban, BigDecimal amount) {
        Account sender = accountRepository.findByIban(senderIban)
                .orElseThrow(() -> new RuntimeException("Gönderen hesap bulunamadı"));

        Account receiver = accountRepository.findByIban(receiverIban)
                .orElseThrow(() -> new RuntimeException("Alıcı hesap bulunamadı"));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz bakiye");
        }

        // Bakiyeleri güncelle
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Transaction oluştur ve kaydet
        Transaction transaction = new Transaction();
        transaction.setAccount(sender);
        transaction.setTargetAccountIban(receiver.getIban());
        transaction.setAmount(amount);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setType("TRANSFER");
        transaction.setAccountIban(sender.getIban());

        transactionRepository.save(transaction);

        // DTO oluştur
        TransactionBaseModel dto = new TransactionBaseModel();
        dto.setAccountIban(sender.getIban());
        dto.setTargetAccountIban(receiver.getIban());
        dto.setAmount(amount);
        dto.setType("TRANSFER");
        dto.setTransactionTime(transaction.getTransactionTime());

        return dto;
    }

    // İşlem geçmişi (hem gönderen hem alıcı işlemleri)
    public List<TransactionBaseModel> getTransactionHistory(String iban) {
        List<Transaction> transactions = transactionRepository.findByAccountIbanOrTargetAccountIban(iban, iban);

        return transactions.stream().map(tx -> {
            TransactionBaseModel dto = new TransactionBaseModel();
            dto.setAccountIban(tx.getAccountIban());
            dto.setTargetAccountIban(tx.getTargetAccountIban());
            dto.setAmount(tx.getAmount());
            dto.setType(tx.getType());
            dto.setTransactionTime(tx.getTransactionTime());
            return dto;
        }).toList();
    }

}
