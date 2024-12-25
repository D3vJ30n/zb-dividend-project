package com.example.demo.exception.impl;

import com.example.demo.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "Invalid password";
    }
}