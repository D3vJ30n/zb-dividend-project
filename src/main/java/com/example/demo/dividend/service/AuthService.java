package com.example.demo.dividend.service;

import com.example.demo.dividend.persist.entity.MemberEntity;
import com.example.demo.dividend.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    public String signin(String username, String password) {
        MemberEntity member = this.memberService.authenticate(username, password);
        return this.tokenProvider.generateToken(member.getUsername(), Collections.singletonList(member.getRole().toString()).toString());
    }

    public MemberEntity signup(String username, String password) {
        return this.memberService.register(username, password);
    }
}