package com.bankapp.bankapp.service;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // ✅ 1. Hesap Açma
    public Account openAccount(User user) {
        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO); // başlangıç bakiyesi 0
        account.setActive(true); // açık hesap
        account.setCreatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    // ✅ 2. Hesap Kapama (Aktifliği false yapar)
    public Account closeAccount(Long accountId) {
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setActive(false); // hesabı pasif hale getir
            return accountRepository.save(account);
        } else {
            throw new RuntimeException("Hesap bulunamadı.");
        }
    }

    // ✅ 3. Kullanıcının Hesaplarını Listele
    public List<Account> getAccountsByUser(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    // ✅ 4. Hesabın Bakiyesini Görüntüle
    public BigDecimal getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı."));
        return account.getBalance();
    }

    // ✅ 5. Hesabı ID ile Getir (gerektiğinde kullanmak için)
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hesap bulunamadı."));
    }

    public Account save(Account account) {
        return null;
    }
}


