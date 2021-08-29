package com.spring.blog.exception.comment;

import org.springframework.http.HttpStatus;

public class CommentDepthException extends CommentException {

    private static final String ERROR_MESSAGE = "유효한 댓글 계층은 1~99 입니다.";
    private static final String ERROR_CODE = "C0003";

    public CommentDepthException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public CommentDepthException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
