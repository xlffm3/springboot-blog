package com.spring.blog.exception.comment;

import org.springframework.http.HttpStatus;

public class CommentContentException extends CommentException {

    private static final String INVALID_COMMENT_CONTENT_MESSAGE =
        "댓글은 1자 이상 140자 이하만 가능합니다.";
    private static final String INVALID_COMMENT_CONTENT_ERROR_CODE = "C0001";

    public CommentContentException() {
        this(
            INVALID_COMMENT_CONTENT_MESSAGE,
            INVALID_COMMENT_CONTENT_ERROR_CODE,
            HttpStatus.BAD_REQUEST
        );
    }

    public CommentContentException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
