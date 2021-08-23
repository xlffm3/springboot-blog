package com.spring.blog.exception.platform;

import com.spring.blog.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public abstract class PlatformException extends ApplicationException {

    protected PlatformException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
