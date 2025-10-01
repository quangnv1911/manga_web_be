package com.manga.manga_web.repository;

import com.manga.manga_web.constant.CrawlSourceValue;
import com.manga.manga_web.entity.CrawlSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CrawlSourceRepository extends JpaRepository<CrawlSource, UUID> {
    CrawlSource findByCrawlSourceValue(CrawlSourceValue crawlSourceValue);
}
