package com.spring.blog.authentication.presentation;

import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.application.dto.TokenResponseDto;
import com.spring.blog.authentication.presentation.dto.OAuthLoginUrlResponse;
import com.spring.blog.authentication.presentation.dto.OAuthTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/authorization/github")
    public ResponseEntity<OAuthLoginUrlResponse> getGithubAuthorizationUrl() {
        String githubAuthorizationUrl = oAuthService.getGithubAuthorizationUrl();
        OAuthLoginUrlResponse oAuthLoginUrlResponse = OAuthLoginUrlResponse.builder()
            .url(githubAuthorizationUrl)
            .build();
        return ResponseEntity.ok(oAuthLoginUrlResponse);
    }

    @GetMapping("/afterlogin")
    public ResponseEntity<OAuthTokenResponse> getLoginToken(@RequestParam String code) {
        TokenResponseDto tokenResponseDto = oAuthService.createToken(code);
        OAuthTokenResponse oAuthTokenResponse = OAuthTokenResponse.builder()
            .token(tokenResponseDto.getToken())
            .userName(tokenResponseDto.getUserName())
            .build();
        return ResponseEntity.ok(oAuthTokenResponse);
    }
}
