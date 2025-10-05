package com.manga.manga_web.repository;

import com.manga.manga_web.entity.MangaChapterDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MangaChapterDetailRepository extends JpaRepository<MangaChapterDetail, UUID> {
    long countByParentChapterTitle(String parentChapterTitle);
}
