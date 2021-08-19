package com.spring.blog.post.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.common.AcceptanceTest;
import com.spring.blog.exception.dto.ApiErrorResponse;
import com.spring.blog.post.presentation.dto.PostRequest;
import com.spring.blog.post.presentation.dto.PostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

class PostAcceptanceTest extends AcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("로그인하지 않은 유저는 게시물을 작성할 수 없다.")
    @Test
    void write_NotLogin_Fail() {
        // given
        PostRequest postRequest = new PostRequest("title", "content");

        // when, then
        webTestClient.post()
            .uri("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(postRequest)
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody(ApiErrorResponse.class)
            .consumeWith(response -> {
                String errorCode = response.getResponseBody().getErrorCode();
                assertThat(errorCode).isEqualTo("A0001");
            });
    }

    @DisplayName("로그인한 유저는 게시물을 작성할 수 있다.")
    @Test
    void write_Login_Success() {
        // given
        String token = requestLoginAndRetrieveToken("kevin");
        PostRequest postRequest = new PostRequest("title", "content");

        // when, then
        requestToWritePost(postRequest, token)
            .expectHeader()
            .location("/api/posts/1");
    }

    @DisplayName("게시물을 단건 조회한다.")
    @Test
    void read_OnePost_Success() {
        // given
        String token = requestLoginAndRetrieveToken("kevin");
        PostRequest postRequest = new PostRequest("title", "content");
        ResponseSpec responseSpec = requestToWritePost(postRequest, token);
        String postId = extractPostId(responseSpec);
        PostResponse postResponse = new PostResponse(
            Long.parseLong(postId),
            "title",
            "content",
            "kevin",
            1L,
            null,
            null
        );

        // when, then
        webTestClient.get()
            .uri("/api/posts/{id}", postId)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PostResponse.class)
            .value(response ->
                assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("createdDate", "modifiedDate")
                    .isEqualTo(postResponse)
            );
    }

    private ResponseSpec requestToWritePost(PostRequest postRequest, String token) {
        return webTestClient.post()
            .uri("/api/posts")
            .headers(header -> header.setBearerAuth(token))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(postRequest)
            .exchange()
            .expectStatus()
            .isCreated();
    }

    private String extractPostId(ResponseSpec responseSpec) {
        String path = responseSpec.expectBody()
            .returnResult()
            .getResponseHeaders()
            .getLocation()
            .getPath();
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }
}
