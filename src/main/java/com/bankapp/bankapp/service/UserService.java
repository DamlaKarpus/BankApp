package com.bankapp.bankapp.service;

import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ✅ eklendi

    // ✅ Constructor injection düzeltildi
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // ✅ atama yapıldı
    }

    // ✅ Şifre encode ediliyor
    public User registerUser(String name, String email, String password) {
        User user = new User();
        user.setUserName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));  // hashli şekilde kaydedilir
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, String name, String email, String password) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUserName(name);
            user.setEmail(email);
            // ✅ Güncellenen parolanın da encode edilmesi gerekir
            user.setPassword(passwordEncoder.encode(password));
            return userRepository.save(user);
        } else {
            throw new RuntimeException("Kullanıcı bulunamadı.");
        }
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Kullanıcı bulunamadı.");
        }
    }
}
