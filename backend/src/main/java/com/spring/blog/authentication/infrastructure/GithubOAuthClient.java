package com.spring.blog.authentication.infrastructure;

import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.authentication.domain.user.UserProfile;
import com.spring.blog.authentication.infrastructure.dto.request.AccessTokenRequestDto;
import com.spring.blog.authentication.infrastructure.dto.response.AccessTokenResponseDto;
import com.spring.blog.authentication.infrastructure.dto.response.UserProfileResponseDto;
import com.spring.blog.exception.platform.PlatformHttpErrorException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

@Component
public class GithubOAuthClient implements OAuthClient {

    private static final String LOGIN_URL_FORMAT =
        "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s";
    private static final String ACCESS_TOKEN_URL =
        "https://github.com/login/oauth/access_token";
    private static final String USER_PROFILE_URL = "https://api.github.com/user";

    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;

    public GithubOAuthClient(
        @Value("${security.github.client.id}") String clientId,
        @Value("${security.github.client.secret}") String clientSecret,
        @Value("${security.github.url.redirect}")String redirectUrl
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
    }

    @Override
    public boolean matches(String name) {
        return name.toUpperCase(Locale.ROOT).matches("GITHUB");
    }

    @Override
    public String getLoginUrl() {
        return String.format(LOGIN_URL_FORMAT, clientId, redirectUrl);
    }

    @Override
    public String getAccessToken(String code) {
        AccessTokenRequestDto accessTokenRequestDto = AccessTokenRequestDto.builder()
            .code(code)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build();
        try {
            return WebClient.create()
                .post()
                .uri(ACCESS_TOKEN_URL)
                .body(Mono.just(accessTokenRequestDto), AccessTokenRequestDto.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AccessTokenResponseDto.class)
                .blockOptional()
                .orElseThrow(PlatformHttpErrorException::new)
                .getAccessToken();
        } catch (WebClientException webClientException) {
            throw new PlatformHttpErrorException();
        }
    }

    @Override
    public UserProfile getUserProfile(String accessToken) {
        try {
            UserProfileResponseDto userProfileResponseDto = WebClient.create()
                .get()
                .uri(USER_PROFILE_URL)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserProfileResponseDto.class)
                .blockOptional()
                .orElseThrow(PlatformHttpErrorException::new);
            return new UserProfile(
                userProfileResponseDto.getName(),
                userProfileResponseDto.getEmail()
            );
        } catch (WebClientException webClientException) {
            throw new PlatformHttpErrorException();
        }
    }
}
