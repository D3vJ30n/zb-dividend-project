package com.example.demo.dividend.config;

import com.example.demo.dividend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Spring Security 설정을 정의하는 구성 클래스
@Configuration
@EnableWebSecurity // Spring Security를 활성화
@EnableMethodSecurity // 메서드 수준의 보안 설정을 활성화
@RequiredArgsConstructor // final 필드를 포함한 생성자를 자동으로 생성하기 위해 사용
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter; // JWT 인증 필터를 주입받아 사용

    // Spring Security 필터 체인을 설정하는 메서드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // HTTP Basic 인증 비활성화
            .httpBasic(httpBasic -> httpBasic.disable())
            // CSRF 보호 비활성화
            .csrf(csrf -> csrf.disable())
            // 세션 관리 정책을 Stateless로 설정
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 인증 및 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/signup", "/auth/signin").permitAll() // 회원가입, 로그인은 인증 없이 접근 가능
                .anyRequest().authenticated()) // 그 외 요청은 인증 필요
            // JWT 인증 필터를 기본 인증 필터 앞에 추가
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build(); // 설정이 완료된 SecurityFilterChain 객체를 반환
    }

    // 비밀번호 암호화를 위한 PasswordEncoder 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 알고리즘을 사용하여 단방향 암호화를 지원
    }
}

/*
### 주요 동작과 이유

1. @RequiredArgsConstructor
   클래스의 final 필드에 대한 생성자를 자동으로 생성하기 위해 사용됨.
   현재 JwtAuthenticationFilter를 생성자 주입 방식으로 설정하기 위해 사용됨.

2. SecurityFilterChain
   Spring Security의 필터 체인을 정의함.
   - HTTP Basic 인증과 CSRF 보호를 비활성화하여 JWT 기반 인증 구조에 맞춤.
   - Stateless한 세션 관리 정책을 설정하여 서버가 인증 상태를 유지하지 않도록 함.
   - 회원가입(/auth/signup)과 로그인(/auth/signin) 요청은 인증 없이 접근을 허용하고, 그 외의 모든 요청은 인증이 필요하도록 설정.
   - JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가하여 사용자 요청에 대한 인증을 처리하도록 함.

3. PasswordEncoder
   비밀번호 암호화를 위해 BCryptPasswordEncoder를 빈으로 등록.
   - 단방향 암호화 알고리즘을 사용하여 비밀번호를 안전하게 암호화.
   - 해시 값을 복호화할 수 없어 보안성이 높음.

---

### 코드의 목적
이 클래스는 Spring Security의 기본 인증 및 권한 설정을 정의하기 위한 구성 클래스임.
JWT를 사용하여 Stateless한 인증 구조를 구현하며, 회원가입 및 로그인과 같은 특정 엔드포인트를 인증 없이 접근할 수 있도록 설정함.
또한, BCryptPasswordEncoder를 통해 안전한 비밀번호 암호화를 지원하여 애플리케이션의 보안을 강화함.
 */
