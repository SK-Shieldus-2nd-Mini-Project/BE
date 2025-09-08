package com.rookies4.MiniProject2.domain.entity;

import com.rookies4.MiniProject2.domain.enums.Role;
import jakarta.persistence.*;
import java.util.Collections;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @OneToMany(mappedBy = "leader", cascade = CascadeType.ALL)
    private List<Group> leadingGroups = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMember> groupMembers = new ArrayList<>();

    @Builder
    public User(String username, String password, String nickname, LocalDate birthdate, String profileImageUrl, Role role) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    // 현재 User 엔티티 정보를 바탕으로 Spring Security의 UserDetails 객체 생성
    public UserDetails toUserDetails() {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(this.role.toString());

        return new org.springframework.security.core.userdetails.User(
                this.username,
                this.password,
                Collections.singleton(grantedAuthority)
        );
    }
}