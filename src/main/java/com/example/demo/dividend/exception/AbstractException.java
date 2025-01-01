package com.example.demo.dividend.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractException extends RuntimeException {
    abstract public int getStatusCode();
    abstract public String getMessage();
}