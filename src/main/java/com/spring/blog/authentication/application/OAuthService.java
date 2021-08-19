package com.spring.blog.authentication.application;

import com.spring.blog.authentication.application.dto.TokenDto;
import com.spring.blog.authentication.domain.JwtTokenProvider;
import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.authentication.domain.user.UserProfile;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OAuthService {

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
    public TokenDto createToken(String code) {
        String accessToken = oAuthClient.getAccessToken(code);
        UserProfile userProfile = oAuthClient.getUserProfile(accessToken);
        String userName = userProfile.getName();
        userRepository.findByName(userName)
            .orElseGet(registerNewUser(userProfile));
        String jwtToken = jwtTokenProvider.createToken(userName);
        return new TokenDto(jwtToken, userName);
    }

    private Supplier<User> registerNewUser(UserProfile userProfile) {
        return () -> {
            User user = new User(userProfile.getName(), userProfile.getProfileImageUrl());
            return userRepository.save(user);
        };
    }
}
