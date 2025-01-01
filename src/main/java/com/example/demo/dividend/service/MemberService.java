package com.example.demo.dividend.service;

import com.example.demo.dividend.exception.impl.AlreadyExistUserException;
import com.example.demo.dividend.exception.impl.InvalidCredentialException;
import com.example.demo.dividend.exception.impl.UserNotFoundException;
import com.example.demo.dividend.persist.entity.MemberEntity;
import com.example.demo.dividend.persist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("사용자 인증 정보 조회 - username: {}", username);
        return (UserDetails) this.memberRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.error("사용자를 찾을 수 없습니다. username: {}", username);
                return new UserNotFoundException();
            });
    }

    @Transactional
    public MemberEntity register(String username, String password) {
        log.info("회원 가입 시작 - username: {}", username);
        if (this.memberRepository.existsByUsername(username)) {
            log.error("이미 존재하는 사용자입니다. username: {}", username);
            throw new AlreadyExistUserException();
        }

        password = this.passwordEncoder.encode(password);
        var member = MemberEntity.builder()
            .username(username)
            .password(password)
            .role(MemberEntity.Role.ROLE_READ)
            .build();

        var result = this.memberRepository.save(member);
        log.info("회원 가입 완료 - username: {}", username);
        return result;
    }

    public MemberEntity authenticate(String username, String password) {
        log.info("로그인 시도 - username: {}", username);
        var user = this.memberRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.error("사용자를 찾을 수 없습니다. username: {}", username);
                return new UserNotFoundException();
            });

        if (!this.passwordEncoder.matches(password, user.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다. username: {}", username);
            throw new InvalidCredentialException();
        }

        log.info("로그인 성공 - username: {}", username);
        return user;
    }
}