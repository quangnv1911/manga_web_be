package com.manga.manga_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NovelCategoryRepository extends JpaRepository<NovelCategoryRepository, UUID> {
}
