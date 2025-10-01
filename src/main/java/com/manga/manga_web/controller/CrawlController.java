package com.manga.manga_web.controller;

import com.gigalike.shared.dto.ApiResponse;
import com.manga.manga_web.dto.request.CrawlTriggerReq;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crawl")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CrawlController {

    @PostMapping("/manga")
    public ResponseEntity<ApiResponse<?>> crawlData(@Valid @RequestBody CrawlTriggerReq req) {
        var response = "This is a public endpoint. Registration logic goes here.";
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }
}
