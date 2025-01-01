package com.example.demo.dividend.exception.impl;

import com.example.demo.dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

    @Override
    public String getMessage() {
        return "아이디 또는 비밀번호가 일치하지 않습니다.";
    }
}
