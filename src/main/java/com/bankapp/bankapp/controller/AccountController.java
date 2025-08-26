package com.bankapp.bankapp.controller;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.service.AccountService;
import com.bankapp.bankapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    // ✅ Hesap aç (POST) - Kullanıcı ID ve opsiyonel hesap adı ile
    @PostMapping("/open/{userId}")
    public ResponseEntity<Account> openAccount(@PathVariable Long userId, @RequestBody(required = false) Account requestAccount) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Account account = accountService.openAccount(userOptional.get());

        // Eğer istekte name varsa onu kullan
        if (requestAccount != null && requestAccount.getName() != null && !requestAccount.getName().isBlank()) {
            account.setName(requestAccount.getName());
            accountService.closeAccount(account.getIban()); // kaydı güncelle
        }

        return ResponseEntity.ok(account);
    }

    // ✅ Hesap kapama (PUT) - IBAN ile
    @PutMapping("/close/{iban}")
    public ResponseEntity<Account> closeAccount(@PathVariable String iban) {
        try {
            Account account = accountService.closeAccount(iban);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Kullanıcının tüm hesaplarını listele (GET)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUser(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUser(userId);
        if (accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(accounts);
    }

    // ✅ Hesap bakiyesini getir (GET) - IBAN ile
    @GetMapping("/{iban}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String iban) {
        try {
            BigDecimal balance = accountService.getBalance(iban);
            return ResponseEntity.ok(balance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Hesabı IBAN ile getir (GET)
    @GetMapping("/{iban}")
    public ResponseEntity<Account> getAccountByIban(@PathVariable String iban) {
        try {
            Account account = accountService.getAccountByIban(iban);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
