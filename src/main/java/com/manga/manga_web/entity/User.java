package com.manga.manga_web.entity;

import com.manga.manga_web.base.BaseEntity;
import com.manga.manga_web.constant.RoleValue;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Data
@SQLDelete(sql = "UPDATE users SET is_delete = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity implements UserDetails {
    @Column(unique = true)
    String username;

    @Column(unique = true, nullable = false)
    String email;

    @Column
    String password;

    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "Role")
    RoleValue role;

    @Column(name = "is_enabled")
    boolean enabled = true;

    @Column(name = "is_account_non_expired")
    boolean accountNonExpired = true;

    @Column(name = "is_account_non_locked")
    boolean accountNonLocked = true;

    @Column(name = "is_credentials_non_expired")
    boolean credentialsNonExpired = true;

    @Column(name = "avatar")
    String avatar;

    @Column(name = "ip_valid")
    String ipValid;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<UserLibraryNovel> userLibraryNovels;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<UserLibraryManga> userLibraryMangas;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }


}
