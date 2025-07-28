package com.bankapp.bankapp.service;

import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsService(UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 🔁 Email ile kullanıcıyı bul
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        // ✅ UserDetails döndür (email üzerinden kimliklendirme yapılacak)
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}
