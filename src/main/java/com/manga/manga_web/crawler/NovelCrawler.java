package com.manga.manga_web.crawler;

import com.manga.manga_web.constant.CrawlSourceValue;
import com.manga.manga_web.entity.CrawlSource;
import com.manga.manga_web.repository.CrawlSourceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NovelCrawler {
    CrawlSourceRepository crawlSourceRepository;
    NovelCrawler self;
    /**
     * Hako re
     *
     * */
    @Async
    public void crawlHako() {
        System.out.println("crawling hako...");
        CrawlSource crawlSource = crawlSourceRepository.findByCrawlSourceValue(CrawlSourceValue.CMANGA);
    }

    @Async
    public void crawlAll() {
        self.crawlHako();
    }
}
