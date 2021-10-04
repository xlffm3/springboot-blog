package com.spring.blog.common.mock;

import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.authentication.domain.user.UserProfile;
import java.util.Locale;

public class MockGithubOAuthClient implements OAuthClient {

    @Override
    public boolean matches(String name) {
        return name.toUpperCase(Locale.ROOT).equals("GITHUB");
    }

    @Override
    public String getLoginUrl() {
        return "https://api.github.com/authorize?";
    }

    @Override
    public String getAccessToken(String code) {
        return code;
    }

    @Override
    public UserProfile getUserProfile(String accessToken) {
        return new UserProfile(accessToken, accessToken + "@naver.com");
    }
}
