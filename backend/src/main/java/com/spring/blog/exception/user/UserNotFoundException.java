package com.spring.blog.exception.user;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserException {

    private static final String USER_NOT_FOUND_ERROR_MESSAGE = "유저를 조회할 수 없습니다.";
    private static final String USER_NOT_FOUND_ERROR_CODE = "U0001";

    public UserNotFoundException() {
        this(USER_NOT_FOUND_ERROR_MESSAGE, USER_NOT_FOUND_ERROR_CODE, HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
