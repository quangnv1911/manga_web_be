package com.manga.manga_web.controller;

import com.manga.manga_web.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<?> healthcheck(Byte status) throws Exception {
        return ResponseEntity.ok(ApiResponse.success("Service is ok"));
    }
}
