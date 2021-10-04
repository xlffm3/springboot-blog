package com.spring.blog.exception.user;

import org.springframework.http.HttpStatus;

public class InvalidUserNameException extends UserException {

    private static final String ERROR_MESSAGE = "유저 이름은 2~10자입니다.";
    private static final String ERROR_CODE = "U0002";

    public InvalidUserNameException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.NOT_FOUND);
    }

    public InvalidUserNameException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
