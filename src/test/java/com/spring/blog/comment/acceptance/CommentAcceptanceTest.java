package com.spring.blog.comment.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.spring.blog.comment.presentation.dto.CommentListResponse;
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

@DisplayName("Comment 인수 테스트")
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
        requestToWriteComment(token, commentWriteRequest, postId)
            .expectBody(CommentResponse.class)
            .value(commentResponse -> {
                assertThat(commentResponse)
                    .extracting("author", "content", "depth")
                    .containsExactly("kevin", "comment!", 1);
            });
    }

    @DisplayName("비로그인 유저는 특정 포스트에 댓글을 작성할 수 없다.")
    @Test
    void write_GuestUser_Fail() {
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

    @DisplayName("로그인한 유저는 특정 댓글에 대댓글을 작성할 수 있다.")
    @Test
    void reply_LoginUser_Success() {
        // given
        String token = requestLoginAndRetrieveToken("kevin");
        PostWriteRequest postWriteRequest = new PostWriteRequest("title", "content");
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("comment!");
        ResponseSpec responseSpec = requestToWritePost(postWriteRequest, token);
        String postId = extractPostId(responseSpec);
        Long commentId = requestToWriteComment(token, commentWriteRequest, postId)
            .expectBody(CommentResponse.class)
            .returnResult()
            .getResponseBody()
            .getId();

        // when, then
        webTestClient.post()
            .uri("/api/posts/{postId}/comments/{commentId}/reply", postId, commentId)
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
                    .containsExactly("kevin", "comment!", 2);
            });
    }

    @DisplayName("비로그인한 유저는 특정 댓글에 대댓글을 작성할 수 없다.")
    @Test
    void reply_GuestUser_Fail() {
        // given
        String token = requestLoginAndRetrieveToken("kevin");
        PostWriteRequest postWriteRequest = new PostWriteRequest("title", "content");
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("comment!");
        ResponseSpec responseSpec = requestToWritePost(postWriteRequest, token);
        String postId = extractPostId(responseSpec);
        Long commentId = requestToWriteComment(token, commentWriteRequest, postId)
            .expectBody(CommentResponse.class)
            .returnResult()
            .getResponseBody()
            .getId();

        // when, then
        webTestClient.post()
            .uri("/api/posts/{postId}/comments/{commentId}/reply", postId, commentId)
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

    /*
    조회 결과
    "first comment"
      ㄴ "second comment"
         ㄴ "third comment"
      ㄴ "fourth comment"
     "fifth comment"
     */
    @DisplayName("계층형 Comment들을 페이지네이션으로 조회한다.")
    @Test
    void readList_Pagination_Success() {
        // given
        String token = requestLoginAndRetrieveToken("kevin");
        PostWriteRequest postWriteRequest = new PostWriteRequest("title", "content");
        ResponseSpec responseSpec = requestToWritePost(postWriteRequest, token);
        String postId = extractPostId(responseSpec);

        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("first comment");
        Long firstCommentId = requestToWriteComment(token, commentWriteRequest, postId)
            .expectBody(CommentResponse.class)
            .returnResult()
            .getResponseBody()
            .getId();

        CommentWriteRequest second = new CommentWriteRequest("second comment");
        Long childCommentId = replyCommentRequest(token, postId, second, firstCommentId)
            .expectBody(CommentResponse.class)
            .returnResult()
            .getResponseBody()
            .getId();

        CommentWriteRequest third = new CommentWriteRequest("third comment");
        replyCommentRequest(token, postId, third, childCommentId);

        CommentWriteRequest fourth = new CommentWriteRequest("fourth comment");
        replyCommentRequest(token, postId, fourth, firstCommentId);

        CommentWriteRequest normal = new CommentWriteRequest("fifth comment");
        requestToWriteComment(token, normal, postId);

        // when
        webTestClient.get()
            .uri("/api/posts/{postId}/comments?page={page}&size={size}&pageBlockCounts={num}",
                postId, 0, 10, 10)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(CommentListResponse.class)
            .value(commentListResponse -> {
                assertThat(commentListResponse.getCommentResponses())
                    .extracting("content", "depth")
                    .containsExactly(
                      tuple("first comment", 1),
                      tuple("second comment", 2),
                      tuple("third comment", 3),
                      tuple("fourth comment", 2),
                      tuple("fifth comment", 1)
                    );
            });
    }

    private ResponseSpec replyCommentRequest(
        String token,
        String postId,
        CommentWriteRequest commentWriteRequest,
        Long commentId
    ) {
        return webTestClient.post()
            .uri("/api/posts/{postId}/comments/{commentId}/reply", postId, commentId)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(commentWriteRequest)
            .exchange();
    }
}
