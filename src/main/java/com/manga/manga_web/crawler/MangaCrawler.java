package com.manga.manga_web.crawler;

import com.manga.manga_web.constant.CrawlSourceValue;
import com.manga.manga_web.entity.CrawlSource;
import com.manga.manga_web.entity.Manga;
import com.manga.manga_web.repository.CrawlSourceRepository;
import com.manga.manga_web.repository.MangaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.manga.manga_web.constant.CrawlConstant.SIMILARITY_THRESHOLD;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MangaCrawler {
    MangaCrawler self;
    CrawlSourceRepository crawlSourceRepository;
    MangaRepository mangaRepository;
    /**
     * Crawl manga from truyenqq
     */
    @Async
    public void crawlTruyenQQ() {
        CrawlSource crawlSource = crawlSourceRepository.findByCrawlSourceValue(CrawlSourceValue.TRUYENQQ);
        System.out.println("Crawling manga from truyenqq...");
    }

    /**
     * Crawl manga from cmanga
     */
    @Async
    public void crawlCmanga() {
        CrawlSource crawlSource = crawlSourceRepository.findByCrawlSourceValue(CrawlSourceValue.CMANGA);
        System.out.println("Crawling manga from cmanga...");
    }

    @Async
    public void crawlAll() {
        self.crawlTruyenQQ();
        self.crawlCmanga();
    }

    private boolean isDuplicateManga(String newTitle, String newAuthor, String url) throws NoSuchAlgorithmException {

        // Kiểm tra fuzzy matching với các title đã lưu
        List<Manga> existingStories = mangaRepository.findAll();
        for (Manga manga : existingStories) {
            double titleSimilarity = FuzzySearch.ratio(newTitle.toLowerCase(), manga.getTitle().toLowerCase()) / 100.0;
            if (titleSimilarity >= SIMILARITY_THRESHOLD) {
                // Có thể thêm kiểm tra author nếu cần
                double authorSimilarity = FuzzySearch.ratio(newAuthor.toLowerCase(), manga.getAuthor().toLowerCase()) / 100.0;
                if (authorSimilarity >= SIMILARITY_THRESHOLD) {
                    return true; // Duplicate dựa trên title + author
                }
            }
        }
        return false;
    }

}
