package com.spring.blog.comment.acceptance;

import com.spring.blog.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

@DisplayName("Comment 인수 테스")
class CommentAcceptanceTest extends AcceptanceTest {

    @Autowired
    private WebTestClient webTestClient;

    //todo : 작성 기능 구현 후 조회 인수 테스트 추가할것
}
