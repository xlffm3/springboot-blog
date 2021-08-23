package com.spring.blog.authentication.presentation;

import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.application.dto.TokenResponseDto;
import com.spring.blog.authentication.presentation.dto.OAuthLoginUrlResponse;
import com.spring.blog.authentication.presentation.dto.OAuthTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/authorization/github")
    public ResponseEntity<OAuthLoginUrlResponse> getGithubAuthorizationUrl() {
        String githubAuthorizationUrl = oAuthService.getGithubAuthorizationUrl();
        OAuthLoginUrlResponse oAuthLoginUrlResponse =
            new OAuthLoginUrlResponse(githubAuthorizationUrl);
        return ResponseEntity.ok(oAuthLoginUrlResponse);
    }

    @GetMapping("/afterlogin")
    public ResponseEntity<OAuthTokenResponse> getLoginToken(@RequestParam String code) {
        TokenResponseDto tokenResponseDto = oAuthService.createToken(code);
        OAuthTokenResponse oAuthTokenResponse =
            new OAuthTokenResponse(tokenResponseDto.getToken(), tokenResponseDto.getUserName());
        return ResponseEntity.ok(oAuthTokenResponse);
    }
}
