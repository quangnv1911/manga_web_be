package com.manga.manga_web.controller;

import com.manga.manga_web.dto.response.ApiResponse;
import com.manga.manga_web.dto.request.RegisterRequest;
import com.manga.manga_web.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicController {
    /**
     * Get all manga in homepage
     *
     * @param request
     * @return
     */
    @PostMapping("/manga")
    public ResponseEntity<ApiResponse<?>> getMangas(@Valid @RequestBody RegisterRequest request) {
        var response = "This is a public endpoint. Registration logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Get all novel in homepage
     *
     * @param request
     * @return
     */
    @PostMapping("/novel")
    public ResponseEntity<ApiResponse<?>> getNovels(@Valid @RequestBody RegisterRequest request) {
        var response = "This is a public endpoint. Registration logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Get manga by id
     *
     * @param mangaId
     * @return
     */
    @PostMapping("/manga/{mangaId}")
    public ResponseEntity<ApiResponse<?>> getManga(@PathVariable UUID mangaId) {
        var response = "This is a public endpoint. Registration logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Get novel by id
     */
    @PostMapping("/novel/{novelId}")
    public ResponseEntity<ApiResponse<?>> getNovel(@PathVariable UUID novelId) {
        var response = "This is a public endpoint. Registration logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Get chapter manga
     */
    @PostMapping("/manga/{mangaId}/{chapterId}")
    public ResponseEntity<ApiResponse<?>> getMangaDetail(@PathVariable UUID mangaId, @PathVariable UUID chapterId) {
        var response = "This is a public endpoint. Registration logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Get chapter novel
     */
    @PostMapping("/novel/{novelId}/{chapterId}")
    public ResponseEntity<ApiResponse<?>> getNovelDetail(@PathVariable UUID novelId, @PathVariable UUID chapterId) {
        var response = "This is a public endpoint. Registration logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Get top manga
     *
     * @return
     */
    @GetMapping("/top-manga")
    public ResponseEntity<ApiResponse<?>> getTopManga() {
        var response = "This is a public endpoint. Top items logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("Top items fetched successfully", response));
    }

    /**
     * Get top novel
     *
     * @return
     */
    @GetMapping("/top-novel")
    public ResponseEntity<ApiResponse<?>> getTopNovel() {
        var response = "This is a public endpoint. Top items logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("Top items fetched successfully", response));
    }

    /**
     * Get random manga
     *
     * @return
     */
    @GetMapping("/random-manga")
    public ResponseEntity<ApiResponse<?>> getRandomManga() {
        var response = "This is a public endpoint. Top items logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("Top items fetched successfully", response));
    }

    /**
     * Get random novel
     *
     * @return
     */
    @GetMapping("/random-novel")
    public ResponseEntity<ApiResponse<?>> getRandomNovel() {
        var response = "This is a public endpoint. Top items logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("Top items fetched successfully", response));
    }
}
