package com.bankapp.bankapp.config; // ← kendi package ismine dikkat et

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Tüm endpoint'lere
                        .allowedOrigins("*") // Tüm origin'lere izin ver (Sadece test için güvenlidir!)
                        .allowedMethods("*") // GET, POST, PUT, DELETE, OPTIONS hepsine izin ver
                        .allowedHeaders("*"); // Tüm header'lara izin ver
            }
        };
    }
}
