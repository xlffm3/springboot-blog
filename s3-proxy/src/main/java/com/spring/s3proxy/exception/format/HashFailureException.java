package com.spring.s3proxy.exception.format;

import com.spring.s3proxy.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class HashFailureException extends ApplicationException {

    private static final String ERROR_MESSAGE = "해시화에 실패했습니다.";
    private static final String ERROR_CODE = "I0003";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public HashFailureException() {
        this(ERROR_MESSAGE, ERROR_CODE, HTTP_STATUS);
    }

    public HashFailureException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
