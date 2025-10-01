package com.manga.manga_web.service;

import com.manga.manga_web.dto.request.CrawlTriggerReq;

public interface ICrawlService {
    void triggerCrawl(CrawlTriggerReq req);
}
