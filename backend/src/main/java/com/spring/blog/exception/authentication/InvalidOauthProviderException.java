package com.spring.blog.exception.authentication;

import org.springframework.http.HttpStatus;

public class InvalidOauthProviderException extends AuthenticationException {

    private static final String ERROR_MESSAGE = "존재하지 않는 OAuth 서비스 제공자입니다.";
    private static final String ERROR_CODE = "A0003";

    public InvalidOauthProviderException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.UNAUTHORIZED);
    }

    public InvalidOauthProviderException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
