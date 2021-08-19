package com.spring.blog.post.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.common.DatabaseCleaner;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.post.application.PostService;
import com.spring.blog.post.application.dto.PostRequestDto;
import com.spring.blog.post.application.dto.PostResponseDto;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.domain.repository.PostRepository;
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
    private PostRepository postRepository;

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
        PostResponseDto expected = new PostResponseDto(
            null,
            "title",
            "content",
            "kevin",
            0L,
            null,
            null
        );
        // then
        assertThat(postResponseDto)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdDate", "modifiedDate")
            .isEqualTo(expected);
    }

    @DisplayName("단건 조회시 게시물이 존재하면 정상적으로 조회하며 조회수가 1 증가한다.")
    @Test
    void readById_ValidId_Success() {
        // given
        User savedUser = userRepository.save(new User("kevin", "image"));
        Post post = new Post(new PostContent("title", "content"), savedUser);
        postRepository.save(post);

        // when
        PostResponseDto expected = new PostResponseDto(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getAuthorName(),
            post.getViewCounts() + 1,
            null,
            null
        );
        PostResponseDto postResponseDto = postService.readById(post.getId());

        // then
        assertThat(postResponseDto)
            .usingRecursiveComparison()
            .ignoringFields("createdDate", "modifiedDate")
            .isEqualTo(expected);
    }

    @DisplayName("단건 조회시 게시물이 없으면 예외가 발생한다.")
    @Test
    void readById_Invalid_ExceptionThrown() {
        // given, when, then
        assertThatCode(() -> postService.readById(13221L))
            .isInstanceOf(PostNotFoundException.class)
            .hasMessage("게시글을 조회할 수 없습니다.")
            .hasFieldOrPropertyWithValue("errorCode", "P0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);
    }
}
