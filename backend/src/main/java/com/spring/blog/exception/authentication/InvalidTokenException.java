package com.spring.blog.exception.authentication;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AuthenticationException {

    private static final String INVALID_TOKEN_MESSAGE = "유효하지 않은 토큰입니다.";
    private static final String INVALID_TOKEN_CODE = "A0001";

    public InvalidTokenException() {
        this(INVALID_TOKEN_MESSAGE, INVALID_TOKEN_CODE, HttpStatus.UNAUTHORIZED);
    }

    public InvalidTokenException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
