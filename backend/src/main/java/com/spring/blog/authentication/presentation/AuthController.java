package com.spring.blog.authentication.presentation;

import com.spring.blog.authentication.application.AuthService;
import com.spring.blog.authentication.application.dto.TokenResponseDto;
import com.spring.blog.authentication.presentation.dto.OAuthLoginUrlResponse;
import com.spring.blog.authentication.presentation.dto.OAuthTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthController {

    private final AuthService authService;

    @GetMapping("/authorization/{oauthProvider}")
    public ResponseEntity<OAuthLoginUrlResponse> getOAuthLoginUrl(
        @PathVariable String oauthProvider
    ) {
        String oauthLoginUrl = authService.getOAuthLoginUrl(oauthProvider);
        OAuthLoginUrlResponse oAuthLoginUrlResponse = OAuthLoginUrlResponse.builder()
            .url(oauthLoginUrl)
            .build();
        return ResponseEntity.ok(oAuthLoginUrlResponse);
    }

    @GetMapping("/{oauthProvider}/login")
    public ResponseEntity<OAuthTokenResponse> loginByOauth(
        @PathVariable String oauthProvider,
        @RequestParam String code
    ) {
        TokenResponseDto tokenResponseDto = authService.loginByOauth(oauthProvider, code);
        OAuthTokenResponse oAuthTokenResponse = OAuthTokenResponse.builder()
            .token(tokenResponseDto.getToken())
            .userName(tokenResponseDto.getUserName())
            .build();
        return ResponseEntity.ok(oAuthTokenResponse);
    }
}
