package com.manga.manga_web.dto.response;

import com.manga.manga_web.entity.Manga;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MangaRes {
    String title;
    String author;


    public static MangaRes convertFromEntity(Manga manga) {
        return MangaRes.builder()
                .title(manga.getTitle())
                .author(manga.getAuthor())
                .build();
    }
}