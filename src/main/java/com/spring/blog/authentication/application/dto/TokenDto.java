package com.spring.blog.authentication.application.dto;

public class TokenDto {

    private String token;
    private String userName;

    private TokenDto() {
    }

    public TokenDto(String token, String userName) {
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
