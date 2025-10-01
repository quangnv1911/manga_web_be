package com.manga.manga_web.repository;

import com.manga.manga_web.entity.Manga;
import com.manga.manga_web.entity.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MangaRepository extends JpaRepository<Manga, UUID> {
    Optional<Manga> findFirstByTitle(String title);
}
