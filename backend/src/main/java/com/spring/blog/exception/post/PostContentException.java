package com.spring.blog.exception.post;

import org.springframework.http.HttpStatus;

public class PostContentException extends PostException {

    private static final String ERROR_MESSAGE = "게시글 내용은 1~10000자만 가능합니다.";
    private static final String ERROR_CODE = "P0002";

    public PostContentException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public PostContentException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
