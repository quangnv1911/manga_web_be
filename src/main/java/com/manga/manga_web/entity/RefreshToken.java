package com.manga.manga_web.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    @Column(name = "is_revoked")
    boolean revoked = false;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
