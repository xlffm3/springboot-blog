package com.spring.blog.exception.user;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserException {

    private static final String ERROR_MESSAGE = "유저를 조회할 수 없습니다.";
    private static final String ERROR_CODE = "U0001";

    public UserNotFoundException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
