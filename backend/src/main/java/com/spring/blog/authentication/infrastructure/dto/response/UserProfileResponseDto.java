package com.spring.blog.authentication.infrastructure.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProfileResponseDto {

    @JsonProperty("login")
    private String name;

    @JsonProperty("avatar_url")
    private String profileImageUrl;
}
