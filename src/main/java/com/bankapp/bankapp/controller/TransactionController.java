package com.bankapp.bankapp.controller;

import com.bankapp.bankapp.entity.TransactionBaseModel;
import com.bankapp.bankapp.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Para yatırma
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> request) {
        try {
            String accountIban = (String) request.get("accountIban");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            TransactionBaseModel transaction = transactionService.deposit(accountIban, amount);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Para yatırma başarılı");
            response.put("transaction", transaction);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "transaction", null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Sunucu hatası",
                    "transaction", null
            ));
        }
    }

    // Para çekme
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> request) {
        try {
            String accountIban = (String) request.get("accountIban");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            TransactionBaseModel transaction = transactionService.withdraw(accountIban, amount);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Para çekme başarılı");
            response.put("transaction", transaction);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "transaction", null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Sunucu hatası",
                    "transaction", null
            ));
        }
    }

    // Para gönderme (havale/transfer)
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, Object> request) {
        try {
            String senderIban = (String) request.get("accountIban");
            String receiverIban = (String) request.get("targetAccountIban");
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            TransactionBaseModel transaction = transactionService.transfer(senderIban, receiverIban, amount);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Transfer başarılı");
            response.put("transaction", transaction);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "transaction", null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Sunucu hatası",
                    "transaction", null
            ));
        }
    }

    // İşlem geçmişi görüntüleme (IBAN body'den alınacak)
    @PostMapping("/history")
    public ResponseEntity<?> getTransactionHistory(@RequestBody Map<String, String> request) {
        try {
            String accountIban = request.get("accountIban");
            List<TransactionBaseModel> transactions = transactionService.getTransactionHistory(accountIban);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "İşlem geçmişi getirildi");
            response.put("transactions", transactions);

            if (transactions.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Sunucu hatası",
                    "transactions", null
            ));
        }
    }


}
