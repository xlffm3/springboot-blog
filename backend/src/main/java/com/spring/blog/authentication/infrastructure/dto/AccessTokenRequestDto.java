package com.spring.blog.authentication.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenRequestDto {

    private String code;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    private AccessTokenRequestDto() {
    }

    public AccessTokenRequestDto(String code, String clientId, String clientSecret) {
        this.code = code;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getCode() {
        return code;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
