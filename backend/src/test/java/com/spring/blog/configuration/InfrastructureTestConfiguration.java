package com.spring.blog.configuration;

import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.common.mock.MockGithubOAuthClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class InfrastructureTestConfiguration {

    @Bean
    public OAuthClient oAuthClient() {
        return new MockGithubOAuthClient();
    }
}
