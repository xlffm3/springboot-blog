package com.spring.blog.post.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.common.AcceptanceTest;
import com.spring.blog.exception.dto.ApiErrorResponse;
import com.spring.blog.post.presentation.dto.PostListResponse;
import com.spring.blog.post.presentation.dto.PostWriteRequest;
import com.spring.blog.post.presentation.dto.PostResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@DisplayName("Post 인수 테스트")
class PostAcceptanceTest extends AcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("로그인하지 않은 유저는 게시물을 작성할 수 없다.")
    @Test
    void write_NotLogin_Fail() {
        // given
        PostWriteRequest postWriteRequest = new PostWriteRequest("title", "content");

        // when, then
        webTestClient.post()
            .uri("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(postWriteRequest)
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
        PostWriteRequest postWriteRequest = new PostWriteRequest("title", "content");

        // when, then
        requestToWritePost(postWriteRequest, token)
            .expectHeader()
            .location("/api/posts/1");
    }

    @DisplayName("게시물을 단건 조회한다.")
    @Test
    void read_OnePost_Success() {
        // given
        String token = requestLoginAndRetrieveToken("kevin");
        PostWriteRequest postWriteRequest = new PostWriteRequest("title", "content");
        requestToWritePost(postWriteRequest, token);
        ResponseSpec responseSpec = requestToWritePost(postWriteRequest, token);
        String postId = extractPostId(responseSpec);
        PostResponse postResponse = new PostResponse(
            Long.parseLong(postId), "title", "content", "kevin", 1L, null, null
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

    @DisplayName("게시물 목록을 페이지네이션으로 최신순 조회한다.")
    @Test
    void readList_OrderByDateDesc_Success() {
        // given
        String token = requestLoginAndRetrieveToken("kevin");
        PostWriteRequest postWriteRequest = new PostWriteRequest("title", "content");
        for (int i = 0; i < 15; i++) {
            requestToWritePost(postWriteRequest, token);
        }

        // when
        webTestClient.get()
            .uri("/api/posts?page={page}&size={size}&pageBlockCounts={num}", 3, 3, 3)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PostListResponse.class)
            .value(postListResponse -> {
                List<PostResponse> postResponse = postListResponse.getPostResponses();
                assertThat(postResponse)
                    .extracting("id")
                    .containsExactly(6L, 5L, 4L);
                assertThat(postListResponse)
                    .extracting("startPage", "endPage", "prev", "next")
                    .containsExactly(4, 5, true, false);
            });
    }

    private ResponseSpec requestToWritePost(PostWriteRequest postWriteRequest, String token) {
        return webTestClient.post()
            .uri("/api/posts")
            .headers(header -> header.setBearerAuth(token))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(postWriteRequest)
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
