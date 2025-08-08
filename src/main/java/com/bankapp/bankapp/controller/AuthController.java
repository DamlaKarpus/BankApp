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
@CrossOrigin(origins = "*") // frontend için CORS açıldı
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // --- DTO'lar ---
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private Long userId;

        public AuthResponse(String token, Long userId) {
            this.token = token;
            this.userId = userId;
        }

        public String getToken() { return token; }
        public Long getUserId() { return userId; }
    }

    // --- Login ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername());

            // Kullanıcıyı al ve token'ı kaydet
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            user.setToken(token); // token alanını User entity’de eklemiş olmalısın
            userService.updateUser(user); // token kaydediliyor

            return ResponseEntity.ok(new AuthResponse(token, user.getId()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Geçersiz email veya şifre");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Giriş sırasında hata oluştu");
        }
    }

    // --- Register ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(
                    user.getUserName(),
                    user.getEmail(),
                    user.getPassword()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Kayıt başarılı");
            response.put("userId", savedUser.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // --- Logout ---
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String email) {
        // Kullanıcının token'ını temizle
        userService.findByEmail(email).ifPresent(u -> {
            u.setToken(null);
            userService.updateUser(u);
        });
        return ResponseEntity.ok("Çıkış yapıldı. Token silindi.");
    }
}
