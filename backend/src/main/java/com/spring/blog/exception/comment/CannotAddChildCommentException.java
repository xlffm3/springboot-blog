package com.spring.blog.exception.comment;

import org.springframework.http.HttpStatus;

public class CannotAddChildCommentException extends CommentException {

    private static final String CANNOT_ADD_CHILD_COMMENT_MESSAGE =
        "대댓글을 추가할 수 없습니다.";
    private static final String CANNOT_ADD_CHILD_COMMENT_ERROR_CODE = "C0002";

    public CannotAddChildCommentException() {
        this(
            CANNOT_ADD_CHILD_COMMENT_MESSAGE,
            CANNOT_ADD_CHILD_COMMENT_ERROR_CODE,
            HttpStatus.BAD_REQUEST
        );
    }

    public CannotAddChildCommentException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
