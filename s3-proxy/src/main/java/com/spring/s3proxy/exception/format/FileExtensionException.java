package com.spring.s3proxy.exception.format;

import com.spring.s3proxy.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class FileExtensionException extends ApplicationException {

    private static final String ERROR_MESSAGE = "유효하지 못한 확장자입니다.";
    private static final String ERROR_CODE = "I0002";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;

    public FileExtensionException() {
        this(ERROR_MESSAGE, ERROR_CODE, HTTP_STATUS);
    }

    public FileExtensionException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
