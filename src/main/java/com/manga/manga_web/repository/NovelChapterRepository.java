package com.manga.manga_web.repository;

import com.manga.manga_web.entity.NovelChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NovelChapterRepository extends JpaRepository<NovelChapter, UUID> {
}
