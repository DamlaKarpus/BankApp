package com.bankapp.bankapp.controller;

import com.bankapp.bankapp.entity.TransactionBaseModel;
import com.bankapp.bankapp.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // ✅ Para yatırma
    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> deposit(@RequestBody Map<String, Object> request) {
        try {
            String accountIban = (String) request.get("accountIban");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            TransactionBaseModel transaction = transactionService.deposit(accountIban, amount);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Para yatırma başarılı",
                    "transaction", transaction
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "transaction", null
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Sunucu hatası",
                    "transaction", null
            ));
        }
    }

    // ✅ Para çekme
    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(@RequestBody Map<String, Object> request) {
        try {
            String accountIban = (String) request.get("accountIban");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            TransactionBaseModel transaction = transactionService.withdraw(accountIban, amount);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Para çekme başarılı",
                    "transaction", transaction
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "transaction", null
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Sunucu hatası",
                    "transaction", null
            ));
        }
    }

    // ✅ Para gönderme (Havale/Transfer)
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transfer(@RequestBody Map<String, Object> request) {
        try {
            String senderIban = (String) request.get("accountIban");
            String receiverIban = (String) request.get("targetAccountIban");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());

            TransactionBaseModel transaction = transactionService.transfer(senderIban, receiverIban, amount);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Transfer başarılı",
                    "transaction", transaction
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "transaction", null
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Sunucu hatası",
                    "transaction", null
            ));
        }
    }

    // ✅ İşlem geçmişi
    @PostMapping("/history")
    public ResponseEntity<Map<String, Object>> getTransactionHistory(@RequestBody Map<String, String> request) {
        try {
            String accountIban = request.get("accountIban");
            List<TransactionBaseModel> transactions = transactionService.getTransactionHistory(accountIban);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", transactions.isEmpty() ? "Hiç işlem bulunamadı" : "İşlem geçmişi getirildi",
                    "transactions", transactions
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Sunucu hatası",
                    "transactions", null
            ));
        }
    }
}
