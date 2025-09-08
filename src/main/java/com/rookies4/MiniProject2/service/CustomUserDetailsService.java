package com.rookies4.MiniProject2.service;

import com.rookies4.MiniProject2.domain.entity.User;
import com.rookies4.MiniProject2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username(사용자 아이디)을 기준으로 UserRepository 에서 사용자 정보를 찾는다.
        return userRepository.findByUsername(username)
                .map(User::toUserDetails) // 사용자가 존재하면 UserDetails 객체로 변환
                .orElseThrow(() -> new UsernameNotFoundException("[ERROR] 해당 유저를 찾을 수 없습니다."));
    }
}
