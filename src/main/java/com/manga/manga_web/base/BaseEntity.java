package com.manga.manga_web.base;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseEntity extends AuditEntity{

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
}