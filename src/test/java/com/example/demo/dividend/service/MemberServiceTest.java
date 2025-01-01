package com.example.demo.dividend.service;

import com.example.demo.dividend.exception.impl.AlreadyExistUserException;
import com.example.demo.dividend.exception.impl.InvalidCredentialException;
import com.example.demo.dividend.exception.impl.UserNotFoundException;
import com.example.demo.dividend.persist.entity.MemberEntity;
import com.example.demo.dividend.persist.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원 등록 성공 테스트")
    void 회원_등록_성공() {
        // given
        String username = "testuser";
        String password = "password123!";
        String encodedPassword = "encodedPassword";
        
        MemberEntity member = MemberEntity.builder()
            .username(username)
            .password(encodedPassword)
            .role(MemberEntity.Role.ROLE_READ)
            .build();

        when(memberRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(member);

        // when
        MemberEntity result = memberService.register(username, password);

        // then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(MemberEntity.Role.ROLE_READ, result.getRole());
        
        verify(memberRepository).existsByUsername(username);
        verify(passwordEncoder).encode(password);
        verify(memberRepository).save(any(MemberEntity.class));
    }

    @Test
    @DisplayName("중복된 사용자명으로 회원 등록 시 예외 발생")
    void 중복_사용자명_회원_등록() {
        // given
        String username = "testuser";
        String password = "password123!";

        when(memberRepository.existsByUsername(username)).thenReturn(true);

        // when & then
        assertThrows(AlreadyExistUserException.class,
            () -> memberService.register(username, password));
        
        verify(memberRepository).existsByUsername(username);
        verify(passwordEncoder, never()).encode(anyString());
        verify(memberRepository, never()).save(any(MemberEntity.class));
    }

    @Test
    @DisplayName("인증 성공 테스트")
    void 인증_성공() {
        // given
        String username = "testuser";
        String password = "password123!";
        String encodedPassword = "encodedPassword";
        
        MemberEntity member = MemberEntity.builder()
            .username(username)
            .password(encodedPassword)
            .role(MemberEntity.Role.ROLE_READ)
            .build();

        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // when
        MemberEntity result = memberService.authenticate(username, password);

        // then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(encodedPassword, result.getPassword());
        
        verify(memberRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 인증 시도시 예외 발생")
    void 존재하지_않는_사용자_인증() {
        // given
        String username = "unknown";
        String password = "password123!";

        when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
            () -> memberService.authenticate(username, password));
        
        verify(memberRepository).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 인증 시도시 예외 발생")
    void 잘못된_비밀번호_인증() {
        // given
        String username = "testuser";
        String password = "wrongpassword";
        String encodedPassword = "encodedPassword";
        
        MemberEntity member = MemberEntity.builder()
            .username(username)
            .password(encodedPassword)
            .role(MemberEntity.Role.ROLE_READ)
            .build();

        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // when & then
        assertThrows(InvalidCredentialException.class,
            () -> memberService.authenticate(username, password));
        
        verify(memberRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    @DisplayName("사용자명으로 회원 조회 성공")
    void loadUserByUsername_성공() {
        // given
        String username = "testuser";
        String encodedPassword = "encodedPassword";
        
        MemberEntity member = MemberEntity.builder()
            .username(username)
            .password(encodedPassword)
            .role(MemberEntity.Role.ROLE_READ)
            .build();

        when(memberRepository.findByUsername(username)).thenReturn(Optional.of(member));

        // when
        var result = memberService.loadUserByUsername(username);

        // then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        
        verify(memberRepository).findByUsername(username);
    }

    @Test
    @DisplayName("존재하지 않는 사용자명으로 회원 조회시 예외 발생")
    void loadUserByUsername_실패() {
        // given
        String username = "unknown";

        when(memberRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
            () -> memberService.loadUserByUsername(username));
        
        verify(memberRepository).findByUsername(username);
    }
}
