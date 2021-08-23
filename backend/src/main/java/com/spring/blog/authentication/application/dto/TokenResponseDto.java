package com.spring.blog.authentication.application.dto;

public class TokenResponseDto {

    private String token;
    private String userName;

    private TokenResponseDto() {
    }

    public TokenResponseDto(String token, String userName) {
        this.token = token;
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public String getUserName() {
        return userName;
    }
}
