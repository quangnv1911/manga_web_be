package com.manga.manga_web.dto.request;

import com.manga.manga_web.constant.SortBy;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class PagingRequest {
    Integer page;
    Integer pageSize;
    @Enumerated(EnumType.STRING)
    SortBy sortBy;
    Boolean isAsc;
}
