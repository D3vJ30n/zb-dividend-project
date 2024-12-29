package com.example.demo.dividend.service;

import com.example.demo.dividend.persist.entity.MemberEntity;
import com.example.demo.dividend.persist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) this.memberRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    public MemberEntity register(String username, String password) {
        boolean exists = this.memberRepository.existsByUsername(username);
        if (exists) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        password = this.passwordEncoder.encode(password);
        var member = MemberEntity.builder()
            .username(username)
            .password(password)
            .role(MemberEntity.Role.ROLE_READ)
            .build();

        return this.memberRepository.save(member);
    }

    public MemberEntity authenticate(String username, String password) {
        var user = this.memberRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 ID 입니다."));

        if (!this.passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}