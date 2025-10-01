package com.manga.manga_web.service.impl;

import com.manga.manga_web.dto.data.UserDto;
import com.manga.manga_web.entity.User;
import com.manga.manga_web.exception.NotFoundException;
import com.manga.manga_web.exception.UserNotFoundException;
import com.manga.manga_web.repository.UserRepository;
import com.manga.manga_web.service.IUserService;
import com.manga.manga_web.util.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {
    UserRepository userRepository;

    @Override
    public UserDto getCurrentUser() {
        return findUserDtoByUsername(SecurityUtil.getCurrentUsername());
    }

    @Override
    public UserDto getUserById(UUID userId) {
        var user = findUserById(userId);
        return UserDto.fromUser(user);
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private UserDto findUserDtoByUsername(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return UserDto.fromUser(user);
    }

}
