package com.spring.blog.exception.post;

import org.springframework.http.HttpStatus;

public class PostNotFoundException extends PostException {

    private static final String ERROR_MESSAGE = "게시글을 조회할 수 없습니다.";
    private static final String ERROR_CODE = "P0001";

    public PostNotFoundException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.NOT_FOUND);
    }

    public PostNotFoundException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
