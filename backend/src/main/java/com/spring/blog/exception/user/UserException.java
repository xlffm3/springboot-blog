package com.spring.blog.exception.user;

import com.spring.blog.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class UserException extends ApplicationException {

    protected UserException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
