package com.manga.manga_web.controller;

import com.manga.manga_web.service.IUserService;
import com.gigalike.shared.dto.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    IUserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMe() {
        var user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Get current user successfully", user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable UUID userId) {
        var user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("Get user successfully", user));
    }

    @PostMapping("/add-manga-library/{mangaId}")
    public ResponseEntity<ApiResponse<?>> addMangaIntoUserLib(@PathVariable UUID mangaId) {
        var response = "abc";
        return ResponseEntity.ok(ApiResponse.success("Get user successfully", response));
    }

    @PostMapping("/add-novel-library/{novelId}")
    public ResponseEntity<ApiResponse<?>> addNovelIntoUserLib(@PathVariable UUID novelId) {
        var response = "abc";
        return ResponseEntity.ok(ApiResponse.success("Get user successfully", response));
    }
}
