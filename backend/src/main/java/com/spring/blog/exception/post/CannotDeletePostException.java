package com.spring.blog.exception.post;

import org.springframework.http.HttpStatus;

public class CannotDeletePostException extends PostException {

    private static final String ERROR_MESSAGE = "게시물을 삭제할 수 없습니다.";
    private static final String ERROR_CODE = "P0004";

    public CannotDeletePostException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public CannotDeletePostException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
