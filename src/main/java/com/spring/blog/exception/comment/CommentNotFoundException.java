package com.spring.blog.exception.comment;

import com.spring.blog.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends ApplicationException {

    private static final String ERROR_MESSAGE = "댓글을 조회할 수 없습니다.";
    private static final String ERROR_CODE = "C0001";

    public CommentNotFoundException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.NOT_FOUND);
    }

    public CommentNotFoundException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
