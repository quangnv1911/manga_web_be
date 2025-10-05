package com.manga.manga_web.repository;

import com.manga.manga_web.entity.MangaChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MangaChapterRepository extends JpaRepository<MangaChapter, UUID> {
    boolean existsBySourceUrl(String sourceUrl);

    long countByParentMangaTitle(String parentMangaTitle);
}
