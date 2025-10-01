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
    String domain;
    @Column(name = "crawl_source_value", unique = true)
    CrawlSourceValue crawlSourceValue;
    @Column(name = "is_active")
    Boolean isActive;
}
