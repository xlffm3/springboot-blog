package com.spring.s3proxy.exception.upload;

import com.spring.s3proxy.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UploadFailureException extends ApplicationException {

    private static final String ERROR_CODE = "I0001";
    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String MESSAGE = "업로드에 실패했습니다.";

    public UploadFailureException() {
        this(MESSAGE, ERROR_CODE, HTTP_STATUS);
    }

    public UploadFailureException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
