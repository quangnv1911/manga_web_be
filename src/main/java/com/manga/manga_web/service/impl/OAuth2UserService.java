package com.manga.manga_web.service.impl;

import com.manga.manga_web.constant.RoleValue;
import com.manga.manga_web.entity.User;
import com.manga.manga_web.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OAuth2UserService extends DefaultOAuth2UserService {

    UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String email = oauth2User.getAttribute("email");
        String googleId = oauth2User.getAttribute("sub");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");
        String profilePicture = oauth2User.getAttribute("picture");

        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isEmpty()) {
            // Check if user exists with same email
            existingUser = userRepository.findByEmail(email);
            
            if (existingUser.isPresent()) {
                // Link Google account to existing user
                User user = existingUser.get();
                if (user.getAvatar() == null) {
                    user.setAvatar(profilePicture);
                }
                userRepository.save(user);
            } else {
                // Create new user
                User newUser = User.builder()
                        .username(email) // Use email as username for OAuth users
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatar(profilePicture)
                        .password("") // Empty password for OAuth users
                        .role(RoleValue.USER)
                        .enabled(true)
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .build();
                
                userRepository.save(newUser);
            }
        }

        return oauth2User;
    }
}
