package com.bankapp.bankapp.init;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IbanInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;

    public IbanInitializer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        accountRepository.findAll().forEach(account -> {
            if (account.getIban() == null || account.getIban().isEmpty()) {
                account.setIban(generateNumericIban());
                accountRepository.save(account);
            }
        });
    }

    // --- Sadece sayılardan oluşan ve Türkiye IBAN formatına uygun IBAN üret ---
    private String generateNumericIban() {
        StringBuilder iban = new StringBuilder("TR"); // TR ile başlar
        for (int i = 0; i < 24; i++) { // TR’den sonra 24 haneli sayı ekle -> toplam 26 karakter
            int digit = (int) (Math.random() * 10); // 0-9 arası sayı
            iban.append(digit);
        }
        return iban.toString();
    }
}
