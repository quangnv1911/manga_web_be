package com.manga.manga_web.repository;

import com.manga.manga_web.entity.NovelCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NovelCategoryRepository extends JpaRepository<NovelCategory, UUID> {
}
