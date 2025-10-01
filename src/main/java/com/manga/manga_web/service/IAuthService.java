package com.manga.manga_web.service;

import com.manga.manga_web.dto.request.LoginGoogleRequest;
import com.manga.manga_web.dto.response.AuthResponse;
import com.manga.manga_web.dto.request.LoginRequest;
import com.manga.manga_web.dto.request.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(HttpServletRequest httpRequest, LoginRequest request);
    AuthResponse loginGoogle(HttpServletRequest httpRequest,LoginGoogleRequest request);
    void logout(String refreshToken);
    AuthResponse refreshToken(String refreshToken);

}
