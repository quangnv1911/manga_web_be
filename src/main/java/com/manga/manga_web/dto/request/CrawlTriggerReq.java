package com.manga.manga_web.dto.request;

import com.manga.manga_web.constant.CrawlSourceValue;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CrawlTriggerReq {
    @Enumerated(EnumType.STRING)
    List<CrawlSourceValue> crawlSourceValue;
}
