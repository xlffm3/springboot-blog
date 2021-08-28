package com.spring.blog.exception.comment;

import org.springframework.http.HttpStatus;

public class CannotEditCommentException extends CommentException {

    private static final String ERROR_MESSAGE = "댓글을 수정할 수 없습니다.";
    private static final String ERROR_CODE = "C0004";

    public CannotEditCommentException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public CannotEditCommentException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
