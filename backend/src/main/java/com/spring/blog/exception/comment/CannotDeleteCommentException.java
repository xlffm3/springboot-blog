package com.spring.blog.exception.comment;

import org.springframework.http.HttpStatus;

public class CannotDeleteCommentException extends CommentException {

    private static final String ERROR_MESSAGE = "댓글을 삭제할 수 없습니다.";
    private static final String ERROR_CODE = "C0005";

    public CannotDeleteCommentException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }

    public CannotDeleteCommentException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
