package com.manga.manga_web.entity;

import com.manga.manga_web.base.BaseEntity;
import com.manga.manga_web.constant.CrawlSourceValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "crawl_source")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CrawlSource extends BaseEntity {
    @Column(name = "domain")
    String domain;

    @Column(name = "referer")
    String referrer;

    @Column(name = "user_agent")
    String UserAgent;

    @Column(name = "sec_ch_ua")
    String secCHUA;

    @Column(name = "sec_ch_ua_mobile")
    String secCHUAMobile;

    @Column(name = "sec_ch_ua_platform")
    String secCHUAPlatform;

    @Column(name = "accept")
    String accept;

    @Column(name = "crawl_source_value", unique = true)
    CrawlSourceValue crawlSourceValue;

    @Column(name = "is_active")
    Boolean isActive;
}
