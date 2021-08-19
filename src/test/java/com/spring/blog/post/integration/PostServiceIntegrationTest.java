package com.spring.blog.post.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.common.DatabaseCleaner;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.post.application.PostService;
import com.spring.blog.post.application.dto.PostRequestDto;
import com.spring.blog.post.application.dto.PostResponseDto;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("PostService 통합 테스트")
@ActiveProfiles("test")
@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    @DisplayName("게시물 작성시 회원이 존재하지 않으면 예외가 발생한다.")
    @Test
    void write_UserNotFound_ExceptionThrown() {
        // given
        PostRequestDto postRequestDto =
            new PostRequestDto(32132L, "title", "content");

        // when, then
        assertThatCode(() -> postService.write(postRequestDto))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("유저를 조회할 수 없습니다.")
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);
    }

    @DisplayName("게시물 작성시 회원이 존재하면 정상 저장된다.")
    @Test
    void write_UserFound_Success() {
        // given
        User savedUser = userRepository.save(new User("kevin", "image"));
        PostRequestDto postRequestDto = new PostRequestDto(savedUser.getId(), "title", "content");

        // when
        PostResponseDto postResponseDto = postService.write(postRequestDto);

        // then
        assertThat(postResponseDto)
            .extracting("author", "title", "content", "viewCounts")
            .containsExactly("kevin", "title", "content", 0L);
    }
}
