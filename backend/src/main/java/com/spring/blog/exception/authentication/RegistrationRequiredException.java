package com.spring.blog.exception.authentication;

import org.springframework.http.HttpStatus;

public class RegistrationRequiredException extends AuthenticationException {

    private static final String ERROR_MESSAGE =
        "해당 소셜 계정으로 등록된 회원 정보가 없습니다. "
            + "GitHub 계정의 경우 설정에서 이메일을 등록하셨는지 다시 확인해주세요.";
    private static final String ERROR_CODE = "A0002";

    private final String email;

    public RegistrationRequiredException(String email) {
        this(ERROR_MESSAGE, ERROR_CODE, HttpStatus.BAD_REQUEST, email);
    }

    public RegistrationRequiredException(
        String message,
        String errorCode,
        HttpStatus httpStatus,
        String email
    ) {
        super(message, errorCode, httpStatus);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
