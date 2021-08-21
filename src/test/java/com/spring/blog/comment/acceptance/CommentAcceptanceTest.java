package com.spring.blog.comment.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.comment.presentation.dto.CommentResponse;
import com.spring.blog.comment.presentation.dto.CommentWriteRequest;
import com.spring.blog.common.AcceptanceTest;
import com.spring.blog.exception.dto.ApiErrorResponse;
import com.spring.blog.post.presentation.dto.PostWriteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@DisplayName("Comment 인수 테스")
class CommentAcceptanceTest extends AcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("로그인한 유저는 특정 포스트에 댓글을 작성할 수 있다.")
    @Test
    void write_LoginUser_Success() {
        // given
        String token = requestLoginAndRetrieveToken("kevin");
        PostWriteRequest postWriteRequest = new PostWriteRequest("title", "content");
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("comment!");
        ResponseSpec responseSpec = requestToWritePost(postWriteRequest, token);
        String postId = extractPostId(responseSpec);

        // when, then
        webTestClient.post()
            .uri("/api/posts/{postId}/comments", postId)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(commentWriteRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(CommentResponse.class)
            .value(commentResponse -> {
                assertThat(commentResponse)
                    .extracting("author", "content", "depth")
                    .containsExactly("kevin", "comment!", 1);
            });
    }

    @DisplayName("비로그인 유저는 특정 포스트에 댓글을 작성할 수 없다.")
    @Test
    void write_GuestUser_Success() {
        // given
        String token = requestLoginAndRetrieveToken("kevin");
        PostWriteRequest postWriteRequest = new PostWriteRequest("title", "content");
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("comment!");
        ResponseSpec responseSpec = requestToWritePost(postWriteRequest, token);
        String postId = extractPostId(responseSpec);

        // when, then
        webTestClient.post()
            .uri("/api/posts/{postId}/comments", postId)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(commentWriteRequest)
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody(ApiErrorResponse.class)
            .value(apiErrorResponse ->
                assertThat(apiErrorResponse.getErrorCode()).isEqualTo("A0001")
            );
    }

    //todo : 작성 기능 구현 후 조회 인수 테스트 추가할것
}
