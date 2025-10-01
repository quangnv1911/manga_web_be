package com.manga.manga_web.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginGoogleRequest {
    @NotBlank(message = "Login failed, something is missing")
    private String idToken;
}
