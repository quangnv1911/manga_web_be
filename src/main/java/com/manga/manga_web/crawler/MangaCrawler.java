package com.manga.manga_web.crawler;

import com.manga.manga_web.config.CrawlerConfig;
import com.manga.manga_web.constant.CommonConstant;
import com.manga.manga_web.constant.CrawlSourceValue;
import com.manga.manga_web.entity.CrawlSource;
import com.manga.manga_web.entity.Manga;
import com.manga.manga_web.entity.MangaChapter;
import com.manga.manga_web.entity.MangaChapterDetail;
import com.manga.manga_web.repository.CrawlSourceRepository;
import com.manga.manga_web.repository.MangaRepository;
import com.manga.manga_web.repository.MangaChapterRepository;
import com.manga.manga_web.repository.MangaChapterDetailRepository;
import com.manga.manga_web.service.StorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.manga.manga_web.config.CrawlerConfig.CRAWL_DELAY;
import static com.manga.manga_web.config.CrawlerConfig.CRAWL_TIMEOUT;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MangaCrawler {
    ApplicationContext applicationContext;
    CrawlSourceRepository crawlSourceRepository;
    MangaRepository mangaRepository;
    MangaChapterRepository mangaChapterRepository;
    MangaChapterDetailRepository mangaChapterDetailRepository;
    StorageService storageService;
    RestTemplate restTemplate;

    /**
     * Crawl manga from truyenqq
     */
    @Async
    public void crawlTruyenQQ() {
        CrawlSource crawlSource = crawlSourceRepository.findByCrawlSourceValue(CrawlSourceValue.TRUYENQQ);
        log.info("Start crawling manga from truyenqq: {}", crawlSource);

        String baseUrl = crawlSource + "/truyen-moi-cap-nhat/trang-%d.html";
        int page = 1;
        int maxEmptyPages = 2;
        int emptyInRow = 0;

        while (true) {
            String pageUrl = String.format(baseUrl, page);
            try {
                Document doc = getDocument(pageUrl, crawlSource);

                Elements items = doc.select("ul.list_grid.grid > li");
                if (items == null || items.isEmpty()) {
                    emptyInRow++;
                    if (emptyInRow >= maxEmptyPages) {
                        log.info("No more items found after {} empty pages. Stopping.", emptyInRow);
                        break;
                    }
                    page++;
                    continue;
                }

                emptyInRow = 0;
                int created = 0;
                for (Element li : items) {
                    try {
                        Element titleAnchor = li.selectFirst(".book_info .book_name h3 > a");
                        if (titleAnchor == null)
                            continue;
                        String title = titleAnchor.text().trim();
                        String detailUrl = titleAnchor.absUrl("href");

                        Element img = li.selectFirst(".book_avatar img");
                        String imageUrl = img != null ? img.absUrl("src") : null;

                        // last chapter available but not used right now (can be parsed from
                        // .last_chapter a)

                        // optional: description snippet present in hidden block
                        Element excerpt = li.selectFirst(".more-info .excerpt");
                        String description = excerpt != null ? excerpt.text().trim() : null;

                        // Author rarely present on list page; leave blank
                        String author = "";

                        // Duplicate check by sourceUrl then title
                        Manga manga = mangaRepository.findFirstBySourceUrl(detailUrl)
                                .orElseGet(() -> mangaRepository.findFirstByTitle(title).orElse(null));
                        if (manga == null) {
                            manga = Manga.builder()
                                    .title(title)
                                    .author(author)
                                    .description(description)
                                    .imageUrl(imageUrl)
                                    .crawlFrom(CrawlSourceValue.TRUYENQQ.name())
                                    .sourceUrl(detailUrl)
                                    .build();
                            mangaRepository.save(manga);
                            created++;
                        }

                        // crawl manga detail page for chapters with delay
                        crawlMangaDetailAndChapters(manga, detailUrl, crawlSource);
                        sleepQuietly(CRAWL_DELAY);
                    } catch (Exception itemEx) {
                        log.warn("Failed to parse/save item on page {}: {}", page, itemEx.getMessage());
                    }
                }
                log.info("Crawled page {} from truyenqq, created {} new records", page, created);

                // proceed next page
                page++;
            } catch (Exception e) {
                log.warn("Failed to fetch page {} from truyenqq: {}", page, e.getMessage());
                emptyInRow++;
                if (emptyInRow >= maxEmptyPages) {
                    break;
                }
                page++;
            }
        }

        log.info("Finished crawling truyenqq up to page {}", page - 1);
    }

    private void crawlMangaDetailAndChapters(Manga manga, String detailUrl, CrawlSource crawlSource) {
        try {
            Document detailDoc = getDocument(detailUrl, crawlSource);

            Elements chapterLinks = detailDoc.select(".list_chapter .works-chapter-item .name-chap a");
            for (Element a : chapterLinks) {
                String chapTitle = a.text().trim();
                String chapUrl = a.absUrl("href");

                if (chapUrl.isEmpty())
                    continue;
                if (mangaChapterRepository.existsBySourceUrl(chapUrl)) {
                    continue;
                }

                MangaChapter chapter = MangaChapter.builder()
                        .title(chapTitle)
                        .crawlFrom(CrawlSourceValue.TRUYENQQ.name())
                        .chapterNumber(parseChapterNumber(chapTitle))
                        .sourceUrl(chapUrl)
                        .parentMangaTitle(manga.getTitle())
                        .build();
                chapter = mangaChapterRepository.save(chapter);

                // fetch chapter detail for images
                crawlChapterImages(chapter, crawlSource);
                sleepQuietly(CRAWL_DELAY);
            }
        } catch (Exception e) {
            log.warn("Failed to crawl detail for manga {}: {}", manga.getTitle(), e.getMessage());
        }
    }

    private void crawlChapterImages(MangaChapter chapter, CrawlSource crawlSource) {
        try {
            Document doc = getDocument(chapter.getSourceUrl(), crawlSource);

            Elements imgs = doc.select(".page-chapter img");
            int index = 0;
            for (Element img : imgs) {
                String url = img.hasAttr("data-original") ? img.absUrl("data-original") : img.absUrl("src");
                if (url == null || url.isEmpty())
                    continue;

                String objectName = buildObjectName(chapter, index, url);
                String uploaded = uploadImageToMinio(url, objectName, crawlSource);
                if (uploaded == null)
                    continue;

                MangaChapterDetail detail = MangaChapterDetail.builder()
                        .imageUrl(uploaded)
                        .parentChapterTitle(chapter.getTitle())
                        .pageIndex(index++)
                        .build();
                mangaChapterDetailRepository.save(detail);
            }
        } catch (Exception e) {
            log.warn("Failed to crawl images for chapter {}: {}", chapter.getTitle(), e.getMessage());
        }
    }

    private String buildObjectName(MangaChapter chapter, int index, String sourceUrl) {
        String ext = ".jpg";
        int q = sourceUrl.lastIndexOf('.');
        if (q > -1 && q > sourceUrl.lastIndexOf('/')) {
            ext = sourceUrl.substring(q);
            if (ext.length() > 5)
                ext = ".jpg";
        }
        String safeTitle = chapter.getParentMangaTitle() != null
                ? chapter.getParentMangaTitle().replaceAll("[^a-zA-Z0-9_-]", "_")
                : "manga";
        String safeChap = chapter.getTitle().replaceAll("[^a-zA-Z0-9_-]", "_");
        return String.format("manga/%s/%s/%03d%s", safeTitle, safeChap, index, ext);
    }

    private String uploadImageToMinio(String sourceUrl, String objectName, CrawlSource crawlSource) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", crawlSource.getUserAgent());
            headers.add("Referer", crawlSource.getReferrer());
            headers.add("sec-ch-ua", CommonConstant.SEC_CH_UA);
            headers.add("sec-ch-ua-mobile", CommonConstant.SEC_CH_UA_MOBILE);
            headers.add("sec-ch-ua-platform", CommonConstant.SEC_CH_UA_PLATFORM);
            headers.add("accept", CommonConstant.ACCEPT);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(sourceUrl, HttpMethod.GET, request, byte[].class);
            byte[] bytes = response.getBody();
            String contentType = "image/jpeg";
            return storageService.upload(objectName, new java.io.ByteArrayInputStream(bytes), bytes.length,
                    contentType);
        } catch (Exception e) {
            log.warn("Failed to fetch/upload image {}: {}", sourceUrl, e.getMessage());
            return null;
        }
    }

    private int parseChapterNumber(String title) {
        try {
            String digits = title.replaceAll("\\D", "");
            return digits.isEmpty() ? 0 : Integer.parseInt(digits);
        } catch (Exception e) {
            return 0;
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Crawl manga from cmanga
     */
    @Async
    public void crawlCmanga() {
        log.info("Crawling manga from cmanga (not implemented)");
    }

    @Async
    public void crawlAll() {
        MangaCrawler self = applicationContext.getBean(MangaCrawler.class);
        self.crawlTruyenQQ();
        self.crawlCmanga();
    }


    private Document getDocument(String url, CrawlSource crawlSource) throws Exception {
        return Jsoup.connect(url)
                .userAgent(crawlSource.getUserAgent())
                .referrer(crawlSource.getReferrer())
                .header("sec-ch-ua", crawlSource.getSecCHUA())
                .header("sec-ch-ua-mobile", crawlSource.getSecCHUAMobile())
                .header("sec-ch-ua-platform", crawlSource.getSecCHUAPlatform())
                .header("accept", crawlSource.getAccept())
                .ignoreContentType(true)
                .timeout(CRAWL_TIMEOUT)
                .get();
    }

}
