package com.manga.manga_web.service.impl;

import com.manga.manga_web.constant.RoleValue;
import com.manga.manga_web.dto.request.LoginGoogleRequest;
import com.manga.manga_web.dto.response.AuthResponse;
import com.manga.manga_web.dto.request.LoginRequest;
import com.manga.manga_web.dto.request.RegisterRequest;
import com.manga.manga_web.dto.data.UserDto;
import com.manga.manga_web.entity.RefreshToken;
import com.manga.manga_web.entity.User;
import com.manga.manga_web.repository.RefreshTokenRepository;
import com.manga.manga_web.repository.UserRepository;
import com.manga.manga_web.service.IAuthService;
import com.gigalike.shared.exception.BusinessException;
import com.gigalike.shared.exception.ResourceNotFoundException;
import com.gigalike.shared.util.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.manga.manga_web.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService {

    UserRepository userRepository;
    RefreshTokenRepository refreshTokenRepository;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    @NonFinal
    String googleClientId;

    @Value("${jwt.access-token-expiration:3600000}")
    @NonFinal
    Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}")
    @NonFinal
    Long refreshTokenExpiration;

    @Transactional
    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(RoleValue.USER)
                .enabled(false)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        user = userRepository.save(user);
        log.info("User {} registered successfully", user.getId());

        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse login(HttpServletRequest httpRequest, LoginRequest request) {
        log.info("Attempting to login user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == RoleValue.ADMIN) {
            throw new BadCredentialsException("Admin must login using Google account");
        }

        if (!isIpValid(httpRequest, user)) {
            throw new BusinessException("Invalid IP address");
        }
        log.info("User {} logged in successfully", user.getId());

        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse loginGoogle(HttpServletRequest httpRequest, LoginGoogleRequest request) {

        NetHttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        // Verify token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        try {
            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                throw new BadCredentialsException("Invalid Google token");
            }
            // Extract payload from token
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // 3. Kiểm tra user trong DB
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        log.info("User {} has been created", email);
                        var newUser = User.builder()
                                .username(CommonUtil.concatUserNameFromEmail(email))
                                .email(email)
                                .firstName(name)
                                .role(RoleValue.USER)
                                .enabled(true)
                                .accountNonExpired(true)
                                .accountNonLocked(true)
                                .credentialsNonExpired(true)
                                .build();
                        return userRepository.save(newUser);
                    });
            if (!isIpValid(httpRequest, user)) {
                throw new BusinessException("Invalid IP address");
            }
            if (!user.isEnabled()) {
                throw new BadCredentialsException("User is disabled");
            }
            // Generate JWT token
            return generateAuthResponse(user);
        } catch (GeneralSecurityException | IOException e) {
            log.error("Token issue: {}", e.getMessage());
            throw new RuntimeException("Google token verification failed", e);
        }

    }

    @Transactional
    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Attempting to refresh token of  user: {}", refreshToken);

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (token.isExpired() || token.isRevoked()) {
            refreshTokenRepository.delete(token);
            throw new BusinessException("Refresh token expired or revoked");
        }

        User user = token.getUser();

        // Revoke the old refresh token
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        log.info("Token of user {} refreshed successfully", user.getId());

        return generateAuthResponse(user);
    }

    @Transactional
    @Override
    public void logout(String refreshToken) {
        log.info("Attempting to logout user");

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        log.info("User {} logged out successfully", token.getUser().getId());
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().toString(), user.getUsername());
        String refreshTokenValue = jwtUtil.generateRefreshToken(user);

        // Save refresh token to database
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000)
                .user(UserDto.fromUser(user))
                .build();
    }

    private boolean isIpValid(HttpServletRequest request, User user) {
        String ip = request.getHeader("X-Forwarded-For");
        log.info("Checking ip address for user {} with ip {}", user.getId(), ip);
        List<String> ipList = Arrays.asList(ip.split(","));
        if (ipList.contains(ip)) {
            return true;
        } else {
            log.info("IP address not found for user {} with ip {}", user.getId(), ip);
            return false;
        }
    }


}
