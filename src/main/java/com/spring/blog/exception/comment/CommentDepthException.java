package com.spring.blog.exception.comment;

import org.springframework.http.HttpStatus;

public class CommentDepthException extends CommentException {

    private static final String INVALID_COMMENT_DEPTH_MESSAGE = "유효한 댓글 계층은 1~99 입니다.";
    private static final String INVALID_COMMENT_DEPTH_ERROR_CODE = "C0003";

    public CommentDepthException() {
        this(
            INVALID_COMMENT_DEPTH_MESSAGE,
            INVALID_COMMENT_DEPTH_ERROR_CODE,
            HttpStatus.BAD_REQUEST
        );
    }

    public CommentDepthException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
