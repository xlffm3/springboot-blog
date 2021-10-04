package com.spring.blog.comment.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.spring.blog.comment.presentation.dto.response.CommentListResponse;
import com.spring.blog.comment.presentation.dto.response.CommentResponse;
import com.spring.blog.comment.presentation.dto.request.CommentWriteRequest;
import com.spring.blog.common.AcceptanceTest;
import com.spring.blog.exception.dto.ApiErrorResponse;
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
        api_회원_등록("kevin");
        String token = api_로그인_요청_및_토큰_반환("kevin");
        String postId = api_테스트용_게시물_작성_ID_회수(token);

        // when, then
        api_댓글_작성(token, "comment!", postId)
            .expectBody(CommentResponse.class)
            .value(commentResponse ->
                assertThat(commentResponse)
                    .extracting("author", "content", "depth")
                    .containsExactly("kevin", "comment!", 1L)
            );
    }

    @DisplayName("비로그인 유저는 특정 포스트에 댓글을 작성할 수 없다.")
    @Test
    void write_GuestUser_Fail() {
        // given
        api_회원_등록("kevin");
        String token = api_로그인_요청_및_토큰_반환("kevin");
        String postId = api_테스트용_게시물_작성_ID_회수(token);
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("comment!");

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
            .value(response -> assertThat(response.getErrorCode()).isEqualTo("A0001"));
    }

    @DisplayName("로그인한 유저는 특정 댓글에 대댓글을 작성할 수 있다.")
    @Test
    void reply_LoginUser_Success() {
        // given
        api_회원_등록("kevin");
        String token = api_로그인_요청_및_토큰_반환("kevin");
        String postId = api_테스트용_게시물_작성_ID_회수(token);
        Long commentId = api_테스트용_댓글_작성_ID_회수(token, postId);

        // when, then
        api_대댓글_작성_요청(token, postId, "comment!", commentId)
            .expectBody(CommentResponse.class)
            .value(commentResponse ->
                assertThat(commentResponse)
                    .extracting("author", "content", "depth")
                    .containsExactly("kevin", "comment!", 2L)
            );
    }

    @DisplayName("비로그인한 유저는 특정 댓글에 대댓글을 작성할 수 없다.")
    @Test
    void reply_GuestUser_Fail() {
        // given
        api_회원_등록("kevin");
        String token = api_로그인_요청_및_토큰_반환("kevin");
        String postId = api_테스트용_게시물_작성_ID_회수(token);
        Long commentId = api_테스트용_댓글_작성_ID_회수(token, postId);
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("comment!");

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
            .value(response -> assertThat(response.getErrorCode()).isEqualTo("A0001"));
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
        api_회원_등록("kevin");
        String token = api_로그인_요청_및_토큰_반환("kevin");
        String postId = api_테스트용_게시물_작성_ID_회수(token);

        Long firstCommentId = api_댓글_작성_요청_ID_회수(token, "first comment", postId);
        api_댓글_작성(token, "fifth comment", postId);

        Long childCommentId = api_대댓글_작성_요청_ID_회수(token, postId, "second comment", firstCommentId);
        api_대댓글_작성_요청(token, postId, "third comment", childCommentId);
        api_대댓글_작성_요청(token, postId, "fourth comment", firstCommentId);

        // when
        webTestClient.get()
            .uri("/api/posts/{postId}/comments?page={page}&size={size}&pageBlockCounts={num}",
                postId, 0, 10, 10)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(CommentListResponse.class)
            .value(commentListResponse ->
                assertThat(commentListResponse.getCommentResponses())
                    .extracting("content", "depth")
                    .containsExactly(
                        tuple("first comment", 1L),
                        tuple("second comment", 2L),
                        tuple("third comment", 3L),
                        tuple("fourth comment", 2L),
                        tuple("fifth comment", 1L)
                    )
            );
    }

    @DisplayName("비로그인 유저는 댓글을 수정할 수 없다.")
    @Test
    void edit_GuestUser_Failure() {
        // given
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("hi");

        // when, then
        webTestClient.put()
            .uri("/api/comments/1")
            .contentType(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(commentWriteRequest)
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody(ApiErrorResponse.class)
            .value(response -> assertThat(response.getErrorCode()).isEqualTo("A0001"));
    }

    @DisplayName("로그인 유저는 댓글을 수정할 수 있다.")
    @Test
    void edit_LoginUser_Failure() {
        // given
        api_회원_등록("kevin");
        String token = api_로그인_요청_및_토큰_반환("kevin");
        String postId = api_테스트용_게시물_작성_ID_회수(token);
        Long commentId = api_댓글_작성_요청_ID_회수(token, "first comment", postId);
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("change comment");

        // when, then
        webTestClient.put()
            .uri("/api/comments/{commentId}", commentId)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .contentType(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(commentWriteRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(CommentResponse.class)
            .value(commentResponse ->
                assertThat(commentResponse)
                    .extracting("content")
                    .isEqualTo("change comment")
            );
    }

    @DisplayName("비로그인 유저는 댓글을 삭제할 수 없다.")
    @Test
    void delete_GuestUser_Failure() {
        // given, when, then
        webTestClient.put()
            .uri("/api/comments/1")
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody(ApiErrorResponse.class)
            .value(response -> assertThat(response.getErrorCode()).isEqualTo("A0001"));
    }

    @DisplayName("로그인 유저는 댓글을 삭제할 수 있다.")
    @Test
    void delete_LoginUser_Success() {
        // given
        api_회원_등록("kevin");
        String token = api_로그인_요청_및_토큰_반환("kevin");
        String postId = api_테스트용_게시물_작성_ID_회수(token);
        Long commentId = api_댓글_작성_요청_ID_회수(token, "first comment", postId);

        // when, then
        webTestClient.delete()
            .uri("/api/comments/{commentId}", commentId)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .exchange()
            .expectStatus()
            .isNoContent();
    }

    @DisplayName("대댓글 작성 요청")
    private ResponseSpec api_대댓글_작성_요청(
        String token,
        String postId,
        String content,
        Long commentId
    ) {
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest(content);
        return webTestClient.post()
            .uri("/api/posts/{postId}/comments/{commentId}/reply", postId, commentId)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(commentWriteRequest)
            .exchange();
    }

    @DisplayName("대댓글 작성 요청 및 ID 회수")
    private Long api_대댓글_작성_요청_ID_회수(
        String token,
        String postId,
        String content,
        Long commentId
    ) {
        return api_대댓글_작성_요청(token, postId, content, commentId)
            .expectBody(CommentResponse.class)
            .returnResult()
            .getResponseBody()
            .getId();
    }
}
