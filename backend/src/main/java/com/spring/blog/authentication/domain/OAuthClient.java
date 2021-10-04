package com.spring.blog.authentication.domain;

import com.spring.blog.authentication.domain.user.UserProfile;

public interface OAuthClient {

    boolean matches(String name);

    String getLoginUrl();

    String getAccessToken(String code);

    UserProfile getUserProfile(String accessToken);
}
