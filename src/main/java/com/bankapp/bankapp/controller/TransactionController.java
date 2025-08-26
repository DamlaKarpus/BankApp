package com.bankapp.bankapp.controller;

import com.bankapp.bankapp.entity.Transaction;
import com.bankapp.bankapp.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Para yatırma
    @PostMapping("/deposit")
    public ResponseEntity<Transaction> deposit(@RequestParam String accountIban, @RequestParam double amount) {
        try {
            Transaction transaction = transactionService.deposit(accountIban, amount);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Para çekme
    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(@RequestParam String accountIban, @RequestParam double amount) {
        try {
            Transaction transaction = transactionService.withdraw(accountIban, amount);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Para gönderme (havale)
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestParam String senderIban,
                                                @RequestParam String receiverIban,
                                                @RequestParam double amount) {
        try {
            Transaction transaction = transactionService.transfer(senderIban, receiverIban, amount);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // İşlem geçmişi görüntüleme
    @GetMapping("/history/{accountIban}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable String accountIban) {
        List<Transaction> transactions = transactionService.getTransactionHistory(accountIban);
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactions);
    }
}
