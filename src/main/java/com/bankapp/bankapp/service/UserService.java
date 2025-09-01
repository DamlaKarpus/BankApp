package com.bankapp.bankapp.service;

import com.bankapp.bankapp.entity.Account;
import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.repository.AccountRepository;
import com.bankapp.bankapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       AccountRepository accountRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Kullanıcı kaydı ---
    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Bu email zaten kayıtlı.");
        }

        if (userRepository.findByUserName(name).isPresent()) {
            throw new RuntimeException("Bu kullanıcı adı zaten kullanılıyor.");
        }

        User user = new User();
        user.setUserName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Kullanıcıyı kaydet
        User savedUser = userRepository.save(user);

        // Hesap oluştur ve varsayılan bakiye ata
        Account account = new Account();
        account.setUser(savedUser);
        account.setBalance(new BigDecimal("1000")); // Varsayılan bakiye
        account.setIban(generateNumericIban());
        account.setName(savedUser.getUserName() + " Hesabı");

        accountRepository.save(account);

        return savedUser;
    }

    // --- Sadece sayılardan oluşan IBAN üret ---
    private String generateNumericIban() {
        StringBuilder iban = new StringBuilder("TR");
        for (int i = 0; i < 22; i++) { // TR dahil toplam 24 karakter olacak
            int digit = (int) (Math.random() * 10); // 0-9 arası sayı
            iban.append(digit);
        }
        return iban.toString();
    }

    // --- Kullanıcı bul ---
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // --- Kullanıcıyı token ile bul ---
    public Optional<User> findByToken(String token) {
        return userRepository.findByToken(token);
    }

    // --- Mevcut updateUser metodunu koru ---
    public User updateUser(Long id, String name, String email, String password) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.findByEmail(email)
                            .filter(u -> !u.getId().equals(id))
                            .ifPresent(u -> { throw new RuntimeException("Bu email başka bir kullanıcı tarafından kullanılıyor."); });

                    userRepository.findByUserName(name)
                            .filter(u -> !u.getId().equals(id))
                            .ifPresent(u -> { throw new RuntimeException("Bu kullanıcı adı başka bir kullanıcı tarafından kullanılıyor."); });

                    user.setUserName(name);
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(password));
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
    }

    // --- User objesi ile update ---
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    // --- Token kaydet ---
    public User saveToken(User user, String token) {
        user.setToken(token);
        return userRepository.save(user);
    }

    // --- Kullanıcı sil ---
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Kullanıcı bulunamadı.");
        }
    }
}
