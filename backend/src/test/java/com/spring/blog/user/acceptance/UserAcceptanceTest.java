package com.spring.blog.user.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.common.AcceptanceTest;
import com.spring.blog.exception.dto.ApiErrorResponse;
import com.spring.blog.user.presentation.dto.UserRegistrationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@DisplayName("User 인수 테스트")
class UserAcceptanceTest extends AcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("비로그인 유저는 회원 탈퇴를 진행할 수 없다.")
    @Test
    void withdraw_GuestUser_Failure() {
        // given, when, then
        webTestClient.delete()
            .uri("/api/users")
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody(ApiErrorResponse.class)
            .value(response -> assertThat(response.getErrorCode()).isEqualTo("A0001"));
    }

    @DisplayName("로그인 유저는 회원 탈퇴를 진행할 수 있다.")
    @Test
    void withdraw_LoginUser_Success() {
        // given
        api_회원_등록("kevin");
        String token = api_로그인_요청_및_토큰_반환("kevin");

        // when, then
        webTestClient.delete()
            .uri("/api/users")
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    @DisplayName("OAuth 기반으로 회원 가입을 진행한다.")
    @Test
    void registerByOauth_Success() {
        // given
        UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
            .email("abc@naver.com")
            .name("abc")
            .build();

        // when
        webTestClient.post()
            .uri("/api/users/oauth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(userRegistrationRequest)
            .exchange()
            .expectStatus()
            .isCreated();

        // then
        String token = api_로그인_요청_및_토큰_반환("abc");
        assertThat(token).isNotNull();
    }
}
