package com.spring.blog.post.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.tuple;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.common.DatabaseCleaner;
import com.spring.blog.common.FileFactory;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.post.application.PostService;
import com.spring.blog.post.application.dto.PostDeleteRequestDto;
import com.spring.blog.post.application.dto.PostListRequestDto;
import com.spring.blog.post.application.dto.PostListResponseDto;
import com.spring.blog.post.application.dto.PostResponseDto;
import com.spring.blog.post.application.dto.PostWriteRequestDto;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

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
    private CommentRepository commentRepository;

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
        PostWriteRequestDto postWriteRequestDto =
            new PostWriteRequestDto(32132L, "title", "content", new ArrayList<>());

        // when, then
        assertThatCode(() -> postService.write(postWriteRequestDto))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("유저를 조회할 수 없습니다.")
            .hasFieldOrPropertyWithValue("errorCode", "U0001")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);
    }

    @DisplayName("이미지를 포함한 게시물 작성시 회원이 존재하면 정상 저장된다.")
    @Test
    void write_UserFound_Success() {
        // given
        User savedUser = userRepository.save(new User("kevin", "image"));
        List<MultipartFile> images = FileFactory.getSuccessImageFiles();
        PostWriteRequestDto postWriteRequestDto =
            new PostWriteRequestDto(savedUser.getId(), "title", "content", images);

        // when
        PostResponseDto postResponseDto = postService.write(postWriteRequestDto);
        PostResponseDto expected = new PostResponseDto(
            null,
            "title",
            "content",
            Arrays.asList("testSuccessImage1.png", "testSuccessImage2.png"),
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
        Post post = new Post("title", "content", savedUser);
        post.addImages(Arrays.asList("url1", "url2"));
        postRepository.save(post);

        // when
        PostResponseDto expected = new PostResponseDto(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getImageUrls(),
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

    @DisplayName("게시물 목록을 페이지네이션으로 최신순으로 조회한다.")
    @Test
    void readPostList_OrderByDateDesc_ThreePosts() {
        // given
        List<User> users = Arrays.asList(
            new User("kevin", "hi"),
            new User("kevin2", "hi2"),
            new User("kevin3", "hi3")
        );
        userRepository.saveAll(users);
        List<Post> posts = Arrays.asList(
            new Post("a1", "b", users.get(0)),
            new Post("a2", "b", users.get(1)),
            new Post("a3", "b", users.get(2))
        );
        postRepository.saveAll(posts);
        PostListRequestDto postListRequestDto =
            new PostListRequestDto(0L, 3L, 3L);

        // when
        PostListResponseDto postListResponseDto = postService.readPostList(postListRequestDto);

        // then
        assertThat(postListResponseDto.getPostResponseDtos())
            .extracting("title", "author")
            .containsExactly(
                tuple("a3", "kevin3"),
                tuple("a2", "kevin2"),
                tuple("a1", "kevin")
            );
        assertThat(postListResponseDto)
            .extracting("startPage", "endPage", "next", "prev")
            .containsExactly(1, 1, false, false);
    }

    @DisplayName("게시물과 댓글들을 모두 삭제한다.")
    @Test
    void deletePost_ValidRequest_True() {
        // given
        User user = userRepository.save(new User("kevin", "image"));
        Post post = postRepository.save(new Post("title", "content", user));
        Comment comment = new Comment("comment", post, user);
        comment.updateAsRoot();
        commentRepository.save(comment);
        PostDeleteRequestDto postDeleteRequestDto =
            new PostDeleteRequestDto(post.getId(), user.getId());

        // when
        postService.deletePost(postDeleteRequestDto);

        // then
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
        assertThat(postRepository.findById(post.getId())).isEmpty();
    }
}
