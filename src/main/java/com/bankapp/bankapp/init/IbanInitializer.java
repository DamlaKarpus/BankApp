package com.bankapp.bankapp.init;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
                account.setIban("TR" + UUID.randomUUID().toString().replace("-", "").substring(0, 24));
                accountRepository.save(account);
            }
        });
    }
}
