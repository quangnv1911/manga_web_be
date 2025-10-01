package com.manga.manga_web.service.impl;

import com.manga.manga_web.constant.CrawlSourceValue;
import com.manga.manga_web.crawler.MangaCrawler;
import com.manga.manga_web.crawler.NovelCrawler;
import com.manga.manga_web.dto.request.CrawlTriggerReq;
import com.manga.manga_web.service.ICrawlService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CrawlService implements ICrawlService {
    MangaCrawler mangaCrawler;
    NovelCrawler novelCrawler;

    @Override
    public void triggerCrawl(CrawlTriggerReq req) {
        if (req == null) {
            mangaCrawler.crawlAll();
            novelCrawler.crawlAll();
            return;
        }
        for (CrawlSourceValue source : req.getCrawlSourceValue()) {
            switch (source) {
                case CMANGA:
                    mangaCrawler.crawlCmanga();
                    break;
                case TRUYENQQ:
                    mangaCrawler.crawlTruyenQQ();
                    break;
                case HAKORE:
                    novelCrawler.crawlHako();
                    break;
                default:
                    log.error("Unsupported source: {}", source);
            }
        }
    }
}
