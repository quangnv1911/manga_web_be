package com.manga.manga_web.dto.data;

import com.manga.manga_web.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    UUID id;
    String username;
    String email;
    String firstName;
    String lastName;
    String role;
    BigDecimal balance;
    String avatar;

    public static UserDto fromUser(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .balance(user.getBalance())
                .role(user.getRole().name())
                .avatar(user.getAvatar())
                .build();
    }

}
