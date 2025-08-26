package com.bankapp.bankapp.repository;

import com.bankapp.bankapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Kullanıcının tüm hesaplarını getir
    List<Account> findByUserId(Long userId);

    // IBAN ile hesap bulma
    Optional<Account> findByIban(String iban);
}
