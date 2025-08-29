package com.bankapp.bankapp.controller;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.service.AccountService;
import com.bankapp.bankapp.service.UserService;
import com.bankapp.bankapp.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AccountController(AccountService accountService, UserService userService, JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // --- Kullanıcının tüm hesaplarını token üzerinden getir ---
    @GetMapping("/me")
    public ResponseEntity<?> getAccounts(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token eksik veya hatalı");
            }
            String token = authHeader.substring(7);

            // JWT’den email çıkar
            String email = jwtUtil.extractUsername(token);
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token geçersiz");
            }

            Optional<User> userOptional = userService.findByEmail(email);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı bulunamadı");
            }

            User user = userOptional.get();
            List<Account> accounts = accountService.getAccountsByUser(user.getId());
            if (accounts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Hesaplar alınamadı: " + e.getMessage());
        }
    }
}
