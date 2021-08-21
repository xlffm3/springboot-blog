package com.spring.blog.common;

import com.spring.blog.authentication.presentation.dto.OAuthTokenResponse;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import com.spring.blog.post.presentation.dto.PostWriteRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

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
        databaseCleaner.execute();;
    }

    @DisplayName("로그인 요청")
    public String requestLoginAndRetrieveToken(String userName) {
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

    @DisplayName("게시물 작성 요청")
    public ResponseSpec requestToWritePost(PostWriteRequest postWriteRequest, String token) {
        return webTestClient.post()
            .uri("/api/posts")
            .headers(header -> header.setBearerAuth(token))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(postWriteRequest)
            .exchange()
            .expectStatus()
            .isCreated();
    }

    @DisplayName("게시물 작성 후 ID 확보")
    public String extractPostId(ResponseSpec responseSpec) {
        String path = responseSpec.expectBody()
            .returnResult()
            .getResponseHeaders()
            .getLocation()
            .getPath();
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }
}
