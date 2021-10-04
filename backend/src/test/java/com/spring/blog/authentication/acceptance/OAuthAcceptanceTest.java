package com.spring.blog.authentication.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.authentication.presentation.dto.OAuthLoginUrlResponse;
import com.spring.blog.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@DisplayName("OAuth 인수 테스트")
class OAuthAcceptanceTest extends AcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("Github Login Url을 요청한다.")
    @Test
    void getGithubAuthorizationUrl_Valid_Success() {
        // given
        OAuthLoginUrlResponse expected = OAuthLoginUrlResponse.builder()
            .url("https://api.github.com/authorize?")
            .build();

        // given, when, then
        webTestClient.get()
            .uri("/api/authorization/github")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(OAuthLoginUrlResponse.class)
            .value(response -> assertThat(response).usingRecursiveComparison().isEqualTo(expected));
    }

    @DisplayName("Github Login 완료 후 토큰을 생성한다.")
    @Test
    void getLoginToken_Valid_Success() {
        // given
        api_회원_등록("kevin");

        // when, then
        webTestClient.get()
            .uri("/api/github/login?code={code}", "kevin")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.userName", "kevin");
    }
}
