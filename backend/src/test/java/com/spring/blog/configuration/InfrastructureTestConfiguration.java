package com.spring.blog.configuration;

import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.authentication.domain.repository.OAuthClientRepository;
import com.spring.blog.authentication.domain.repository.OAuthClientRepositoryImpl;
import com.spring.blog.common.mock.MockFileStorage;
import com.spring.blog.common.mock.MockGithubOAuthClient;
import com.spring.blog.post.domain.FileStorage;
import java.util.Arrays;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class InfrastructureTestConfiguration {

    @Bean
    public OAuthClientRepository oAuthClientRepository() {
        return new OAuthClientRepositoryImpl(Arrays.asList(oAuthClient()));
    }

    @Bean
    public OAuthClient oAuthClient() {
        return new MockGithubOAuthClient();
    }

    @Bean
    public FileStorage fileStorage() {
        return new MockFileStorage();
    }
}
