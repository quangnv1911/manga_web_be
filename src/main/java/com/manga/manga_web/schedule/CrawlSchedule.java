package com.manga.manga_web.schedule;

import com.manga.manga_web.crawler.MangaCrawler;
import com.manga.manga_web.crawler.NovelCrawler;
import com.manga.manga_web.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Slf4j
@Component
@Service
public class CrawlSchedule {
    MangaCrawler mangaCrawler;
    NovelCrawler novelCrawler;

    // chạy mỗi 6 tiếng
    @Scheduled(fixedRate = 1000 * 60 * 60 * 6)
    public void autoCrawlData() {
        log.info("Start auto crawl data at {}", CommonUtil.getCurrentDateTime());
        mangaCrawler.crawlAll();
        novelCrawler.crawlAll();
    }
}
