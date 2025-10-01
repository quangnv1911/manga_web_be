package com.manga.manga_web.repository;

import com.manga.manga_web.entity.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NovelRepository extends JpaRepository<Novel, UUID> {
}
