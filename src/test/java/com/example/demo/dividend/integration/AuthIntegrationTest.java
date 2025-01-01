package com.example.demo.dividend.integration;

import com.example.demo.dividend.TestApplication;
import com.example.demo.dividend.config.SecurityConfigTest;
import com.example.demo.dividend.model.Auth;
import com.example.demo.dividend.model.constants.Message;
import com.example.demo.dividend.persist.entity.MemberEntity;
import com.example.demo.dividend.persist.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(SecurityConfigTest.class)
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanup() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {
        // given
        Auth.SignUp request = new Auth.SignUp();
        request.setUsername("testuser");
        request.setPassword("password123!");

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(Message.SUCCESS_SIGNUP));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 아이디")
    void signupFailDuplicate() throws Exception {
        // given
        String username = "testuser";
        String password = "password123!";

        memberRepository.save(MemberEntity.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .role(MemberEntity.Role.ROLE_READ)
            .build());

        Auth.SignUp request = new Auth.SignUp();
        request.setUsername(username);
        request.setPassword(password);

        // when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 성공")
    void signinSuccess() throws Exception {
        // given
        String username = "testuser";
        String password = "password123!";

        memberRepository.save(MemberEntity.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .role(MemberEntity.Role.ROLE_READ)
            .build());

        Auth.SignIn request = new Auth.SignIn();
        request.setUsername(username);
        request.setPassword(password);

        // when & then
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(Message.SUCCESS_SIGNIN))
            .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void signinFailUserNotFound() throws Exception {
        // given
        Auth.SignIn request = new Auth.SignIn();
        request.setUsername("unknown");
        request.setPassword("password123!");

        // when & then
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void signinFailWrongPassword() throws Exception {
        // given
        String username = "testuser";
        String password = "password123!";

        memberRepository.save(MemberEntity.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .role(MemberEntity.Role.ROLE_READ)
            .build());

        Auth.SignIn request = new Auth.SignIn();
        request.setUsername(username);
        request.setPassword("wrongpassword");

        // when & then
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }
}
