package com.bankapp.bankapp.repository;

import com.bankapp.bankapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // E-posta ile kullanıcıyı bulmak için
    Optional<User> findByEmail(String email);

    // Kullanıcı adı ile kullanıcıyı bulmak için
    Optional<User> findByUserName(String username);

    // Token ile kullanıcıyı bulmak için
    Optional<User> findByToken(String token);
}
