package com.manga.manga_web.service.impl;

import com.manga.manga_web.dto.request.MangasReq;
import com.manga.manga_web.dto.response.MangaRes;
import com.manga.manga_web.entity.Manga;
import com.manga.manga_web.repository.MangaRepository;
import com.manga.manga_web.service.IMangaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MangaService implements IMangaService {
    MangaRepository mangaRepository;

    @Override
    public List<MangaRes> getMangas(MangasReq req) {
        Sort sort = Sort.by(String.valueOf(req.getSortBy()));
        if (req.getIsAsc()) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize(), sort);
        Page<Manga> mangaRes = mangaRepository.findAll(pageable);


        return mangaRes.stream().map(MangaRes::convertFromEntity).toList();
    }



}
