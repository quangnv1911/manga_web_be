package com.manga.manga_web.entity;

import com.manga.manga_web.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "novel_categories")
@Data
@SQLDelete(sql = "UPDATE novel_categories SET is_delete = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NovelCategory extends BaseEntity {
    String name;
}
