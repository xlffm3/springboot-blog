package com.spring.blog.exception;

import com.spring.blog.exception.dto.ApiErrorResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvisor {

    private static final String INTERNAL_SERVER_ERROR_CODE = "S0001";

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> applicationException(ApplicationException e) {
        String errorCode = e.getErrorCode();
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(new ApiErrorResponse(errorCode));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> dataAccessException(DataAccessException e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponse(INTERNAL_SERVER_ERROR_CODE));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> runtimeException(RuntimeException e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiErrorResponse(INTERNAL_SERVER_ERROR_CODE));
    }
}
