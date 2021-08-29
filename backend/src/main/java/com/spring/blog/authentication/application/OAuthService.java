package com.spring.blog.authentication.application;

import com.spring.blog.authentication.application.dto.TokenResponseDto;
import com.spring.blog.authentication.domain.JwtTokenProvider;
import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.authentication.domain.user.AnonymousUser;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.authentication.domain.user.UserProfile;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class OAuthService {

    private static final String JWT_TOKEN_KEY_NAME = "userName";

    private final OAuthClient oAuthClient;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public OAuthService(
        OAuthClient oAuthClient,
        UserRepository userRepository,
        JwtTokenProvider jwtTokenProvider
    ) {
        this.oAuthClient = oAuthClient;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String getGithubAuthorizationUrl() {
        return oAuthClient.getLoginUrl();
    }

    @Transactional
    public TokenResponseDto createToken(String code) {
        String accessToken = oAuthClient.getAccessToken(code);
        UserProfile userProfile = oAuthClient.getUserProfile(accessToken);
        String userName = userProfile.getName();
        User user = userRepository.findByName(userName)
            .orElseGet(registerNewUser(userProfile));
        user.activate();
        String jwtToken = jwtTokenProvider.createToken(userName);
        return new TokenResponseDto(jwtToken, userName);
    }

    private Supplier<User> registerNewUser(UserProfile userProfile) {
        return () -> {
            User user = new User(userProfile.getName(), userProfile.getProfileImageUrl());
            return userRepository.save(user);
        };
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public AppUser findRequestUserByToken(String token) {
        if (Objects.isNull(token)) {
            return new AnonymousUser();
        }
        String userName = jwtTokenProvider.getPayloadByKey(token, JWT_TOKEN_KEY_NAME);
        User user = userRepository.findByName(userName)
            .orElseThrow(UserNotFoundException::new);
        return new LoginUser(user.getId(), user.getName());
    }
}
