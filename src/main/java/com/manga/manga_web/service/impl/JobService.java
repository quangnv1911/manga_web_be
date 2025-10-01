package com.manga.manga_web.service.impl;

import com.manga.manga_web.repository.RefreshTokenRepository;
import com.manga.manga_web.service.IJobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobService implements IJobService {
    RefreshTokenRepository refreshTokenRepository;

    @Override
    // Schedule 2h
    @Scheduled(fixedRate = 1000 * 60 * 60 * 2, initialDelay = 1000 * 60 * 60 * 2)
    public void clearExpiredToken() {
        log.info("*** Start clearing expired token ***");
        var tokensExpires = refreshTokenRepository.findRefreshTokenByExpiredIsBefore(LocalDateTime.now());

        if (!tokensExpires.isEmpty()) {
            refreshTokenRepository.deleteAll(tokensExpires);
            log.info("Deleted {} expired tokens", tokensExpires.size());
        } else {
            log.info("No expired tokens found");
        }
        log.info("*** Done clearing expired token ***");
    }
}
