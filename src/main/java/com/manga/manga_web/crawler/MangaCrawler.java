package com.manga.manga_web.crawler;

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

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MangaCrawler {
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122 Safari/537.36";
    static final String REFERER = "https://truyenqqgo.com/";
    public static final String SEC_CH_UA = "\"Chromium\";v=\"140\", \"Not=A?Brand\";v=\"24\", \"Google Chrome\";v=\"140\"";

    public static final String SEC_CH_UA_MOBILE = "?0";

    public static final String SEC_CH_UA_PLATFORM = "\"Windows\"";

    public static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
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

        final String baseUrl = "https://truyenqqgo.com/truyen-moi-cap-nhat/trang-%d.html";
        int page = 1;
        int maxEmptyPages = 2;
        int emptyInRow = 0;

        while (true) {
            String pageUrl = String.format(baseUrl, page);
            try {
                Document doc = Jsoup.connect(pageUrl)
                        .userAgent(USER_AGENT)
                        .referrer(REFERER)
                        .header("sec-ch-ua", SEC_CH_UA)
                        .header("sec-ch-ua-mobile", SEC_CH_UA_MOBILE)
                        .header("sec-ch-ua-platform", SEC_CH_UA_PLATFORM)
                        .header("accept", ACCEPT)
                        .ignoreContentType(true)
                        .timeout(15000)
                        .get();

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
                        crawlMangaDetailAndChapters(manga, detailUrl);
                        sleepQuietly(10000);
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

    private void crawlMangaDetailAndChapters(Manga manga, String detailUrl) {
        try {
            Document detailDoc = Jsoup.connect(detailUrl)
                    .userAgent(USER_AGENT)
                    .referrer(REFERER)
                    .header("sec-ch-ua", SEC_CH_UA)
                    .header("sec-ch-ua-mobile", SEC_CH_UA_MOBILE)
                    .header("sec-ch-ua-platform", SEC_CH_UA_PLATFORM)
                    .header("accept", ACCEPT)
                    .ignoreContentType(true)
                    .timeout(15000)
                    .get();

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
                crawlChapterImages(chapter);
                sleepQuietly(10000);
            }
        } catch (Exception e) {
            log.warn("Failed to crawl detail for manga {}: {}", manga.getTitle(), e.getMessage());
        }
    }

    private void crawlChapterImages(MangaChapter chapter) {
        try {
            Document doc = Jsoup.connect(chapter.getSourceUrl())
                    .userAgent(USER_AGENT)
                    .referrer(REFERER)
                    .header("sec-ch-ua", SEC_CH_UA)
                    .header("sec-ch-ua-mobile", SEC_CH_UA_MOBILE)
                    .header("sec-ch-ua-platform", SEC_CH_UA_PLATFORM)
                    .header("accept", ACCEPT)
                    .ignoreContentType(true)
                    .timeout(20000)
                    .get();

            Elements imgs = doc.select(".page-chapter img");
            int index = 0;
            for (Element img : imgs) {
                String url = img.hasAttr("data-original") ? img.absUrl("data-original") : img.absUrl("src");
                if (url == null || url.isEmpty())
                    continue;

                String objectName = buildObjectName(chapter, index, url);
                String uploaded = uploadImageToMinio(url, objectName);
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

    private String uploadImageToMinio(String sourceUrl, String objectName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", USER_AGENT);
            headers.add("Referer", REFERER);
            headers.add("sec-ch-ua", SEC_CH_UA);
            headers.add("sec-ch-ua-mobile", SEC_CH_UA_MOBILE);
            headers.add("sec-ch-ua-platform", SEC_CH_UA_PLATFORM);
            headers.add("accept", ACCEPT);
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

}
