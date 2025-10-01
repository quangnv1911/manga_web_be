package com.manga.manga_web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
@ComponentScan(basePackages = "com.gigalike")
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("system"); // fallback nếu chưa login
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails userDetails) {
                return Optional.ofNullable(userDetails.getUsername());
            }

            if (principal instanceof String username) {
                return Optional.of(username);
            }

            return Optional.of("unknown");
        };
    }
}
