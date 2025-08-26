package com.bankapp.bankapp.service;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Hesap Açma (IBAN ile)
    public Account openAccount(User user) {
        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO); // başlangıç bakiyesi 0
        account.setActive(true); // açık hesap
        account.setCreatedAt(LocalDateTime.now());

        // IBAN üretimi: TR + UUID ilk 20 karakter
        String iban = "TR" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        account.setIban(iban);

        return accountRepository.save(account);
    }

    // Hesap Kapama (IBAN ile)
    public Account closeAccount(String iban) {
        Account account = getAccountByIban(iban);
        account.setActive(false); // hesabı pasif hale getir
        return accountRepository.save(account);
    }

    // Kullanıcının Hesaplarını Listele
    public List<Account> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    // Hesabın Bakiyesini Görüntüle (IBAN ile)
    public BigDecimal getBalance(String iban) {
        Account account = getAccountByIban(iban);
        return account.getBalance();
    }

    // Hesabı IBAN ile Getir
    public Account getAccountByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı."));
    }
}
