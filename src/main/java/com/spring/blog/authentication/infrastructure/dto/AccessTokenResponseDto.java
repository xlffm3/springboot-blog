package com.spring.blog.authentication.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponseDto {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("scope")
    private String scope;

    private AccessTokenResponseDto() {
    }

    public AccessTokenResponseDto(String accessToken, String tokenType, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getScope() {
        return scope;
    }
}
