package com.manga.manga_web.service;

import com.manga.manga_web.dto.data.UserDto;

import java.util.UUID;

public interface IUserService {
    UserDto getCurrentUser();
    UserDto getUserById(UUID userId);
}
