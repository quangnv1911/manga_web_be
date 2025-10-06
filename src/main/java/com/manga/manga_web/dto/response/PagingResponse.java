package com.manga.manga_web.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class PagingResponse<T> {
    int currentPage;
    int pageSize;
    int totalPages;
    List<T> data;
}
