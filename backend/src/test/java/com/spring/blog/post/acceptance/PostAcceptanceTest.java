package com.spring.blog.post.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.common.AcceptanceTest;
import com.spring.blog.exception.dto.ApiErrorResponse;
import com.spring.blog.post.presentation.dto.response.PostListResponse;
import com.spring.blog.post.presentation.dto.response.PostResponse;
import java.util.Arrays;
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
        // given, when, then
        webTestClient.post()
            .uri("/api/posts")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(generateMultipartWithImages("title", "content"))
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody(ApiErrorResponse.class)
            .value(response -> assertThat(response.getErrorCode()).isEqualTo("A0001"));
    }

    @DisplayName("로그인한 유저는 이미지를 포함한 게시물을 작성할 수 있다.")
    @Test
    void write_LoginWithImage_Success() {
        // given
        String token = api_로그인_요청_및_토큰_반환("kevin");

        // when
        ResponseSpec responseSpec = api_테스트용_게시물_작성(token);
        String postId = 게시물_ID_추출(responseSpec);

        // then
        responseSpec.expectHeader()
            .location("/api/posts/" + postId);
    }

    /*
    단순히 Body에 Null을 주면 테스트 통과 불가능
    Postman 등은 실제로 비어있는 하나의 멀티파트를 전송함.
    InvocableHandlerMethod에 디버그 찍고 확인해본 결과, 테스트가 약간은 수정되어야 함
     */
//    @DisplayName("로그인한 유저는 이미지 없이 게시물을 작성할 수 있다.")
//    @Test
//    void write_LoginWithoutImage_Success() {
//        // given
//        String token = api_로그인_요청_및_토큰_반환("kevin");
//
//        // when
//        ResponseSpec responseSpec = webTestClient.post()
//            .uri("/api/posts")
//            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
//            .contentType(MediaType.MULTIPART_FORM_DATA)
//            .accept(MediaType.APPLICATION_JSON)
//            .bodyValue(generateMultipartWithoutImages("title", "content"))
//            .exchange()
//            .expectStatus()
//            .isOk();
//        String postId = 게시물_ID_추출(responseSpec);
//
//        // then
//        responseSpec.expectHeader()
//            .location("/api/posts" + postId);
//    }

    @DisplayName("게시물을 단건 조회한다.")
    @Test
    void read_OnePost_Success() {
        // given
        String token = api_로그인_요청_및_토큰_반환("kevin");
        api_테스트용_게시물_작성(token);
        String postId = api_게시물_작성_ID_회수("hi", "there", token);
        List<String> urls = Arrays.asList("testSuccessImage1.png", "testSuccessImage2.png");
        PostResponse expectedPostResponse = new PostResponse(
            Long.parseLong(postId), "hi", "there", urls,"kevin",1L, null, null
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
                    .isEqualTo(expectedPostResponse)
            );
    }

    @DisplayName("게시물 목록을 페이지네이션으로 최신순 조회한다.")
    @Test
    void readList_OrderByDateDesc_Success() {
        // given
        String token = api_로그인_요청_및_토큰_반환("kevin");
        for (int i = 0; i < 15; i++) {
            api_테스트용_게시물_작성(token);
        }

        // when, then
        webTestClient.get()
            .uri("/api/posts?page={page}&size={size}&pageBlockCounts={num}", 3, 3, 3)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(PostListResponse.class)
            .value(postListResponse -> {
                assertThat(postListResponse.getSimplePostResponses())
                    .extracting("id")
                    .containsExactly(6L, 5L, 4L);
                assertThat(postListResponse)
                    .extracting("startPage", "endPage", "prev", "next")
                    .containsExactly(4, 5, true, false);
            });
    }

    @DisplayName("비로그인 유저는 게시물 삭제가 불가능하다.")
    @Test
    void delete_GuestUser_Failure() {
        // given, when, then
        webTestClient.delete()
            .uri("/api/posts/1")
            .exchange()
            .expectStatus()
            .isUnauthorized()
            .expectBody(ApiErrorResponse.class)
            .value(response -> assertThat(response.getErrorCode()).isEqualTo("A0001"));
    }

    @DisplayName("로그인 유저는 게시물을 삭제할 수 있다.")
    @Test
    void delete_LoginUser_Failure() {
        // given
        String token = api_로그인_요청_및_토큰_반환("kevin");
        String postId = api_게시물_작성_ID_회수("hi", "there", token);

        // when, then
        webTestClient.delete()
            .uri("/api/posts/{postId}", postId)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .exchange()
            .expectStatus()
            .isNoContent();
    }
}
