package com.spring.blog.exception.post;

import org.springframework.http.HttpStatus;

public class InvalidContentException extends PostException {

    private static final String INVALID_CONTENT_MESSAGE = "게시글 내용은 공백일 수 없습니다.";
    private static final String INVALID_CONTENT_ERROR_CODE = "P0002";

    public InvalidContentException() {
        this(INVALID_CONTENT_MESSAGE, INVALID_CONTENT_ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public InvalidContentException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
