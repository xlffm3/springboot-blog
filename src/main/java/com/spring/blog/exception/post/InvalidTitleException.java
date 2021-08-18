package com.spring.blog.exception.post;

import org.springframework.http.HttpStatus;

public class InvalidTitleException extends PostException {

    private static final String INVALID_TITLE_MESSAGE =
        "게시글 제목은 공백이거나 100자를 초과할 수 없습니다.";
    private static final String INVALID_TITLE_ERROR_CODE = "P0001";

    public InvalidTitleException() {
        this(INVALID_TITLE_MESSAGE, INVALID_TITLE_ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public InvalidTitleException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
