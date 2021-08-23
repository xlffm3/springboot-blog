package com.spring.blog.exception.post;

import com.spring.blog.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class PostException extends ApplicationException {

    protected PostException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
