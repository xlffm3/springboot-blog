package com.spring.blog.exception.post;

import org.springframework.http.HttpStatus;

public class CannotAddChildPostException extends PostException {

    private static final String INVALID_DEPTH_MESSAGE = "답글을 추가할 수 없습니다.";
    private static final String INVALID_DEPTH_ERROR_CODE = "P0003";

    public CannotAddChildPostException() {
        this(INVALID_DEPTH_MESSAGE, INVALID_DEPTH_ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public CannotAddChildPostException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
