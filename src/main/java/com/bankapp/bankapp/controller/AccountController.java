package com.bankapp.bankapp.controller;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.service.AccountService;
import com.bankapp.bankapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        // İsteğin içinde name varsa onu kullan, yoksa varsayılan ver
        String accountName = (requestAccount != null && requestAccount.getName() != null && !requestAccount.getName().isBlank())
                ? requestAccount.getName()
                : "Yeni Hesap";

        Account account = new Account();
        account.setUser(userOptional.get());
        account.setName(accountName); // ✅ doğru alan
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(LocalDateTime.now());
        account.setActive(true);

        Account savedAccount = accountService.save(account);
        return ResponseEntity.ok(savedAccount);
    }

    // ✅ Hesap kapama (PUT)
    @PutMapping("/close/{accountId}")
    public ResponseEntity<Account> closeAccount(@PathVariable Long accountId) {
        try {
            Account account = accountService.closeAccount(accountId);
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

    // ✅ Hesap bakiyesini getir (GET)
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long accountId) {
        try {
            BigDecimal balance = accountService.getBalance(accountId);
            return ResponseEntity.ok(balance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Hesabı ID ile getir (GET)
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long accountId) {
        try {
            Account account = accountService.getAccountById(accountId);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
