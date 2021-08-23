package com.spring.blog.common;

import com.spring.blog.authentication.presentation.dto.OAuthTokenResponse;
import com.spring.blog.comment.presentation.dto.CommentResponse;
import com.spring.blog.comment.presentation.dto.CommentWriteRequest;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Import(InfrastructureTestConfiguration.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
        ;
    }

    @DisplayName("로그인 요청 및 토큰 반환")
    protected String api_로그인_요청_및_토큰_반환(String userName) {
        return webTestClient.get()
            .uri("/api/afterlogin?code={code}", userName)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(OAuthTokenResponse.class)
            .returnResult()
            .getResponseBody()
            .getToken();
    }

    @DisplayName("테스트용 게시물 작성")
    protected ResponseSpec api_테스트용_게시물_작성(String token) {
        return api_게시물_작성("test title", "test content", token);
    }

    @DisplayName("게시물 작성")
    protected ResponseSpec api_게시물_작성(String title, String content, String token) {
        return webTestClient.post()
            .uri("/api/posts")
            .headers(header -> header.setBearerAuth(token))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(generateMultipartWithImages(title, content))
            .exchange()
            .expectStatus()
            .isCreated();
    }

    @DisplayName("멀티파트 Body (이미지 포함)")
    public static MultiValueMap<String, Object> generateMultipartWithImages(
        String title,
        String content
    ) {
        List<MultipartFile> files = FileFactory.getSuccessImageFiles();
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("title", title);
        multiValueMap.add("content", content);
        files.forEach(file -> {
            try {
                Resource resource =
                    new FileSystemResource(file.getBytes(), file.getOriginalFilename());
                multiValueMap.add("files", resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return multiValueMap;
    }

    @DisplayName("멀티파트 Body (이미지 미포함)")
    public static MultiValueMap<String, Object> generateMultipartWithoutImages(
        String title,
        String content
    ) {
        List<MultipartFile> files = new ArrayList<>();
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("title", title);
        multiValueMap.add("content", content);
        return multiValueMap;
    }

    @DisplayName("테스트용 게시물 작성 요청 및 ID 회수")
    protected String api_테스트용_게시물_작성_ID_회수(String token) {
        ResponseSpec responseSpec = api_테스트용_게시물_작성(token);
        return 게시물_ID_추출(responseSpec);
    }

    @DisplayName("게시물 작성 요청 및 ID 회수")
    protected String api_게시물_작성_ID_회수(String title, String content, String token) {
        ResponseSpec responseSpec = api_게시물_작성(title, content, token);
        return 게시물_ID_추출(responseSpec);
    }

    @DisplayName("게시물 ID 추출")
    protected String 게시물_ID_추출(ResponseSpec responseSpec) {
        String path = responseSpec.expectBody()
            .returnResult()
            .getResponseHeaders()
            .getLocation()
            .getPath();
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    @DisplayName("테스트용 댓글 작성")
    protected ResponseSpec api_테스트용_댓글_작성(String token, String postId) {
        return api_댓글_작성(token, "test comment", postId);
    }

    @DisplayName("댓글 작성")
    protected ResponseSpec api_댓글_작성(
        String token,
        String content,
        String postId
    ) {
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest(content);
        return webTestClient.post()
            .uri("/api/posts/{postId}/comments", postId)
            .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(commentWriteRequest)
            .exchange()
            .expectStatus()
            .isOk();
    }

    @DisplayName("테스트용 댓글 작성 및 ID 회수")
    protected Long api_테스트용_댓글_작성_ID_회수(String token, String postId) {
        ResponseSpec responseSpec = api_테스트용_댓글_작성(token, postId);
        return extractCommentId(responseSpec);
    }

    @DisplayName("댓글 작성 요청 및 ID 회수")
    protected Long api_댓글_작성_요청_ID_회수(
        String token,
        String content,
        String postId
    ) {
        ResponseSpec responseSpec = api_댓글_작성(token, content, postId);
        return extractCommentId(responseSpec);
    }

    private Long extractCommentId(ResponseSpec responseSpec) {
        return responseSpec
            .expectBody(CommentResponse.class)
            .returnResult()
            .getResponseBody()
            .getId();
    }

    public static class FileSystemResource extends ByteArrayResource {

        private String fileName;

        public FileSystemResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.fileName = filename;
        }

        public String getFilename() {
            return fileName;
        }

        public void setFilename(String fileName) {
            this.fileName = fileName;
        }
    }
}
