package com.spring.blog.exception.user;

import org.springframework.http.HttpStatus;

public class NameDuplicationException extends UserException {

    private static final String ERROR_MESSAGE = "중복된 이름입니다.";
    private static final String ERROR_CODE = "U0003";

    public NameDuplicationException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public NameDuplicationException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
