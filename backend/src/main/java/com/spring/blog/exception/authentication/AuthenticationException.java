package com.spring.blog.exception.authentication;

import com.spring.blog.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class AuthenticationException extends ApplicationException {

    protected AuthenticationException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
