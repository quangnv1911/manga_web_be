package com.manga.manga_web.service;

import com.manga.manga_web.dto.request.MangasReq;
import com.manga.manga_web.dto.response.MangaChapterRes;
import com.manga.manga_web.dto.response.MangaDetailRes;
import com.manga.manga_web.dto.response.MangaRes;

import java.util.List;
import java.util.UUID;

public interface IMangaService {
    List<MangaRes> getMangas(MangasReq req);
    MangaDetailRes getManga(UUID id);
    List<MangaChapterRes> getMangaDetail(UUID id, UUID chapterId);
}
