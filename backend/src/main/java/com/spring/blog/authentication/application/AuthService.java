package com.spring.blog.authentication.application;

import com.spring.blog.authentication.application.dto.TokenResponseDto;
import com.spring.blog.authentication.domain.JwtTokenProvider;
import com.spring.blog.authentication.domain.OAuthClient;
import com.spring.blog.authentication.domain.repository.OAuthClientRepository;
import com.spring.blog.authentication.domain.user.AnonymousUser;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.authentication.domain.user.UserProfile;
import com.spring.blog.exception.authentication.RegistrationRequiredException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private static final String JWT_TOKEN_KEY_NAME = "userName";

    private final OAuthClientRepository oAuthClientRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String getOAuthLoginUrl(String oauthProvider) {
        return oAuthClientRepository.findByName(oauthProvider)
            .getLoginUrl();
    }

    public TokenResponseDto loginByOauth(String oauthProvider, String code) {
        OAuthClient oAuthClient = oAuthClientRepository.findByName(oauthProvider);
        String accessToken = oAuthClient.getAccessToken(code);
        UserProfile userProfile = oAuthClient.getUserProfile(accessToken);
        String email = userProfile.getEmail();
        User user = userRepository.findActiveUserByEmail(email)
            .orElseThrow(() -> new RegistrationRequiredException(email));
        String userName = user.getName();
        String jwtToken = jwtTokenProvider.createToken(userName);
        return TokenResponseDto.builder()
            .token(jwtToken)
            .userName(userName)
            .build();
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public AppUser findRequestUserByToken(String token) {
        if (Objects.isNull(token)) {
            return new AnonymousUser();
        }
        String userName = jwtTokenProvider.getPayloadByKey(token, JWT_TOKEN_KEY_NAME);
        User user = userRepository.findActiveUserByName(userName)
            .orElseThrow(UserNotFoundException::new);
        return new LoginUser(user.getId(), user.getName());
    }
}
