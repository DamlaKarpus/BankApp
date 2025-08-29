package com.bankapp.bankapp.service;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.repository.AccountRepository;
import com.bankapp.bankapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    // --- Yeni Hesap Aç ---
    public Account openAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());
        account.setIban("TR" + UUID.randomUUID().toString().replace("-", "").substring(0, 20));

        return accountRepository.save(account);
    }

    // --- Hesap Kapama ---
    public Account closeAccount(String iban) {
        Account account = getAccountByIban(iban);
        account.setActive(false);
        return accountRepository.save(account);
    }

    // --- Hesabı Kaydet / Güncelle ---
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    // --- Kullanıcının Tüm Hesapları ---
    public List<Account> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    // --- Token ile Kullanıcının Hesapları ---
    public List<Account> getAccountsByToken(String token) {
        User user = userRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        return accountRepository.findByUserId(user.getId());
    }


    // --- Hesabı IBAN ile Getir ---
    public Account getAccountByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı."));
    }

    // --- Bakiye Görüntüle ---
    public BigDecimal getBalance(String iban) {
        Account account = getAccountByIban(iban);
        return account.getBalance();
    }

    // --- Para Yatır ---
    @Transactional
    public Account deposit(String iban, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Yatırılacak miktar sıfırdan büyük olmalı.");
        }
        Account account = getAccountByIban(iban);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    // --- Para Çek ---
    @Transactional
    public Account withdraw(String iban, BigDecimal amount) {
        Account account = getAccountByIban(iban);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Çekilecek miktar sıfırdan büyük olmalı.");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Yetersiz bakiye.");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }

    // --- Para Transferi ---
    @Transactional
    public void transfer(String fromIban, String toIban, BigDecimal amount) {
        if (fromIban.equals(toIban)) {
            throw new RuntimeException("Kendi hesabınıza transfer yapılamaz.");
        }

        Account fromAccount = getAccountByIban(fromIban);
        Account toAccount = getAccountByIban(toIban);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer miktarı sıfırdan büyük olmalı.");
        }
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Gönderen hesabın bakiyesi yetersiz.");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}
