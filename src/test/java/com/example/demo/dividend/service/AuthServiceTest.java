package com.example.demo.dividend.service;

import com.example.demo.dividend.exception.impl.AlreadyExistUserException;
import com.example.demo.dividend.exception.impl.InvalidCredentialException;
import com.example.demo.dividend.exception.impl.UserNotFoundException;
import com.example.demo.dividend.persist.entity.MemberEntity;
import com.example.demo.dividend.security.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void 회원가입_성공() {
        // given
        String username = "testuser";
        String password = "password123!";
        MemberEntity member = MemberEntity.builder()
            .username(username)
            .password(password)
            .role(MemberEntity.Role.ROLE_READ)
            .build();

        when(memberService.register(username, password)).thenReturn(member);

        // when
        MemberEntity result = authService.signup(username, password);

        // then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void 로그인_성공() {
        // given
        String username = "testuser";
        String password = "password123!";
        String token = "test.jwt.token";
        MemberEntity member = MemberEntity.builder()
            .username(username)
            .password(password)
            .role(MemberEntity.Role.ROLE_READ)
            .build();

        when(memberService.authenticate(username, password)).thenReturn(member);
        when(tokenProvider.generateToken(anyString(), anyString()))
            .thenReturn(token);

        // when
        String result = authService.signin(username, password);

        // then
        assertNotNull(result);
        assertEquals(token, result);
    }

    @Test
    @DisplayName("중복된 사용자명으로 회원가입 시도시 예외 발생")
    void 중복_사용자명_회원가입() {
        // given
        String username = "testuser";
        String password = "password123!";

        when(memberService.register(username, password))
            .thenThrow(new AlreadyExistUserException());

        // when & then
        assertThrows(AlreadyExistUserException.class,
            () -> authService.signup(username, password));
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시도시 예외 발생")
    void 존재하지_않는_사용자_로그인() {
        // given
        String username = "unknown";
        String password = "password123!";

        when(memberService.authenticate(username, password))
            .thenThrow(new UserNotFoundException());

        // when & then
        assertThrows(UserNotFoundException.class,
            () -> authService.signin(username, password));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도시 예외 발생")
    void 잘못된_비밀번호_로그인() {
        // given
        String username = "testuser";
        String password = "wrongpassword";

        when(memberService.authenticate(username, password))
            .thenThrow(new InvalidCredentialException());

        // when & then
        assertThrows(InvalidCredentialException.class,
            () -> authService.signin(username, password));
    }
}
