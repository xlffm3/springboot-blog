package com.spring.blog.authentication.presentation.dto;

public class OAuthTokenResponse {

    private String token;
    private String userName;

    private OAuthTokenResponse() {
    }

    public OAuthTokenResponse(String token, String userName) {
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
