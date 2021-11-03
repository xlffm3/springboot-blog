package com.spring.blog.exception.platform;

import org.springframework.http.HttpStatus;

public class PlatformHttpErrorException extends PlatformException {

    private static final String ERROR_MESSAGE = "외부 플랫폼 연동에 실패했습니다.";
    private static final String ERROR_CODE = "V0001";

    public PlatformHttpErrorException() {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PlatformHttpErrorException(String message) {
        this(message, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public PlatformHttpErrorException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
