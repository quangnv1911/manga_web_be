package com.manga.manga_web.entity;

import com.manga.manga_web.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "manga")
@Data
@SQLDelete(sql = "UPDATE manga SET is_delete = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Manga extends BaseEntity {
    String title;

    String author;

    String description;
    @Column(name = "image_url", columnDefinition = "TEXT")
    String imageUrl;

    @Column(name = "crawl_from")
    String crawlFrom;

    List<MangaChapter> chapters;

}
