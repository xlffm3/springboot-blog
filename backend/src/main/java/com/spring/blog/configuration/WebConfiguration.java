package com.spring.blog.configuration;

import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.presentation.interceptor.AuthenticationInterceptor;
import com.spring.blog.authentication.presentation.resolver.AuthenticationPrincipalArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final OAuthService oAuthService;

    public WebConfiguration(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(oAuthService);
    }

    @Bean
    public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver(oAuthService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
            .addPathPatterns("/api/posts")
            .addPathPatterns("/api/posts/*/comments")
            .addPathPatterns("/api/posts/*/comments/*/reply")
            .addPathPatterns("/api/comments/*");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationPrincipalArgumentResolver());
    }
}
