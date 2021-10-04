package com.spring.blog.exception.user;

import org.springframework.http.HttpStatus;

public class ActiveAccountExistingException extends UserException {

    private static final String ERROR_MESSAGE = "동일한 이메일로 등록된 회원 계정이 존재합니다.";
    private static final String ERROR_CODE = "U0004";

    public ActiveAccountExistingException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public ActiveAccountExistingException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
