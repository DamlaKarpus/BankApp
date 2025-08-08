package com.bankapp.bankapp.controller;

import com.bankapp.bankapp.entity.User;
import com.bankapp.bankapp.service.UserService;
import com.bankapp.bankapp.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // Login için kullanılan DTO (request gövdesi için)
    public static class LoginRequest {
        private String email;       // username yerine email
        private String password;

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername());

            User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı bulunamadı.");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", user.getId());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Geçersiz email veya şifre");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT stateless olduğu için logout işlemi client tarafında yapılır (token silinir)
        return ResponseEntity.ok("Çıkış yapıldı. Token client tarafından silinmeli.");
    }
}
