package com.spring.blog.exception.comment;

import org.springframework.http.HttpStatus;

public class CommentContentException extends CommentException {

    private static final String ERROR_MESSAGE = "댓글은 1자 이상 140자 이하만 가능합니다.";
    private static final String ERROR_CODE = "C0002";

    public CommentContentException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public CommentContentException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
