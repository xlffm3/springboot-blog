package com.spring.blog.exception.post;

import org.springframework.http.HttpStatus;

public class PostTitleException extends PostException {

    private static final String ERROR_MESSAGE =
        "게시글 제목은 공백이거나 100자를 초과할 수 없습니다.";
    private static final String ERROR_CODE = "P0003";

    public PostTitleException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public PostTitleException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
