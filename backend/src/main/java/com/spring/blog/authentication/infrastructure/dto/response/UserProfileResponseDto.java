package com.spring.blog.authentication.infrastructure.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserProfileResponseDto {

    @JsonProperty("login")
    private String name;

    @JsonProperty("avatar_url")
    private String profileImageUrl;

    private UserProfileResponseDto() {
    }

    public UserProfileResponseDto(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
