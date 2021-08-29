package com.spring.blog.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.common.FileFactory;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.post.application.dto.request.PostDeleteRequestDto;
import com.spring.blog.post.application.dto.request.PostListRequestDto;
import com.spring.blog.post.application.dto.response.PostListResponseDto;
import com.spring.blog.post.application.dto.response.PostResponseDto;
import com.spring.blog.post.application.dto.request.PostWriteRequestDto;
import com.spring.blog.post.domain.FileStorage;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("PostService 슬라이스 테스트")
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private FileStorage fileStorage;

    @DisplayName("write 메서드는")
    @Nested
    class Describe_write {

        @DisplayName("작성 요청자 ID에 해당하는 유저가 존재하지 않을 때")
        @Nested
        class Context_user_not_found {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                PostWriteRequestDto postWriteRequestDto = new PostWriteRequestDto(1L, "title",
                    "content", new ArrayList<>());
                given(userRepository.findById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> postService.write(postWriteRequestDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("유저를 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(userRepository, times(1)).findById(1L);
            }
        }

        @DisplayName("작성 요청자에 해당하는 유저가 존재할 때")
        @Nested
        class Context_user_found {

            @DisplayName("게시물을 정상 저장한다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                List<MultipartFile> images = FileFactory.getSuccessImageFiles();
                PostWriteRequestDto postWriteRequestDto =
                    new PostWriteRequestDto(1L, "title", "content", images);
                User user = new User("kevin", "image");
                Post post = new Post(1L, "title", "content", user);
                post.addImages(Arrays.asList("url1", "url2"));

                given(userRepository.findById(1L)).willReturn(Optional.of(user));
                given(postRepository.save(any(Post.class))).willReturn(post);
                given(fileStorage.store(images, "kevin"))
                    .willReturn(Arrays.asList("url1", "url2"));

                // when
                PostResponseDto postResponseDto = postService.write(postWriteRequestDto);
                PostResponseDto expected = new PostResponseDto(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    Arrays.asList("url1", "url2"),
                    post.getAuthorName(),
                    post.getViewCounts(),
                    null,
                    null);

                // then
                assertThat(postResponseDto)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);

                verify(userRepository, times(1)).findById(1L);
                verify(fileStorage, times(1)).store(images, "kevin");
                verify(postRepository, times(1)).save(any(Post.class));
            }
        }
    }

    @DisplayName("readById 메서드는")
    @Nested
    class Describe_readById {

        @DisplayName("ID에 해당하는 Post가 존재할 때")
        @Nested
        class Context_valid_id {

            @DisplayName("정상적으로 조회되며 조회수가 1 증가한다.")
            @Test
            void it_returns_response() {
                // given
                Long id = 13212L;
                User user = new User(id, "kevin", "image");
                Post post = new Post(1L, "title", "content", user);
                given(postRepository.findWithAuthorById(id)).willReturn(Optional.of(post));

                // when
                PostResponseDto expected = new PostResponseDto(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    Collections.emptyList(),
                    post.getAuthorName(),
                    post.getViewCounts() + 1,
                    null,
                    null
                );
                PostResponseDto postResponseDto = postService.readById(id);

                // then
                assertThat(postResponseDto)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);

                verify(postRepository, times(1)).findWithAuthorById(id);
            }
        }

        @DisplayName("ID에 해당하는 Post가 없을 때")
        @Nested
        class Context_invalid_id {

            @DisplayName("예외를 발생시킨다.")
            @Test
            void it_throws_PostNotFoundException() {
                // given
                given(postRepository.findWithAuthorById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> postService.readById(1L))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("게시글을 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(postRepository, times(1)).findWithAuthorById(1L);
            }
        }
    }

    @DisplayName("readPostList 메서드는")
    @Nested
    class Describe_readPostList {

        @DisplayName("페이지네이션 정보가 주어질 때")
        @Nested
        class Context_given_pagination {

            @DisplayName("Post를 최신순으로 페이지네이션하며, 페이지 정보를 함께 반환한다.")
            @Test
            void it_returns_posts_with_pagination_information() {
                // given
                PostListRequestDto postListRequestDto =
                    new PostListRequestDto(1L, 5L, 3L);
                List<Post> mockPosts = Arrays.asList(
                    new Post("a3", "b3", new User("kevin3", "image")),
                    new Post("a2", "b2", new User("kevin2", "image")),
                    new Post("a", "b", new User("kevin", "image"))
                );
                given(postRepository.findPostsOrderByDateDesc(any(Pageable.class)))
                    .willReturn(mockPosts);
                given(postRepository.count()).willReturn(23L);

                // when
                PostListResponseDto postListResponseDto =
                    postService.readPostList(postListRequestDto);

                // then
                assertThat(postListResponseDto)
                    .extracting("startPage", "endPage", "next", "prev")
                    .containsExactly(1, 3, true, false);
                assertThat(postListResponseDto.getSimplePostResponseDtos())
                    .extracting("title", "author")
                    .containsExactly(
                        tuple("a3", "kevin3"),
                        tuple("a2", "kevin2"),
                        tuple("a", "kevin")
                    );

                verify(postRepository, times(1))
                    .findPostsOrderByDateDesc(any(Pageable.class));
                verify(postRepository, times(1)).count();
            }
        }
    }

    @DisplayName("deletePost 메서드는")
    @Nested
    class Describe_deletePost {

        @DisplayName("Post가 존재하지 않을 때")
        @Nested
        class Context_post_not_found {

            @DisplayName("예외를 발생시킨다.")
            @Test
            void it_throws_PostNotFoundException() {
                // given
                PostDeleteRequestDto postDeleteRequestDto =
                    new PostDeleteRequestDto(1L, 1L);
                given(postRepository.findWithAuthorById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> postService.deletePost(postDeleteRequestDto))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("게시글을 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(postRepository, times(1)).findWithAuthorById(1L);
            }
        }

        @DisplayName("유저가 존재하지 않을 때")
        @Nested
        class Context_user_different {

            @DisplayName("예외를 발생시킨다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                PostDeleteRequestDto postDeleteRequestDto =
                    new PostDeleteRequestDto(1L, 1L);
                Post post = new Post("title", "content", null);
                given(postRepository.findWithAuthorById(1L)).willReturn(Optional.of(post));
                given(userRepository.findById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> postService.deletePost(postDeleteRequestDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("유저를 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(postRepository, times(1)).findWithAuthorById(1L);
                verify(userRepository, times(1)).findById(1L);
            }
        }

        @DisplayName("정상적인 요청인 경우")
        @Nested
        class Context_valid_request {

            @DisplayName("게시물과 댓글들을 모두 삭제한다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                PostDeleteRequestDto postDeleteRequestDto =
                    new PostDeleteRequestDto(1L, 1L);
                User user = new User(1L, "kevin", "image");
                Post post = new Post("title", "content", user);
                given(postRepository.findWithAuthorById(1L)).willReturn(Optional.of(post));
                given(userRepository.findById(1L)).willReturn(Optional.of(user));
                Mockito.doNothing().when(commentRepository).deleteAllByPost(post);

                // when
                postService.deletePost(postDeleteRequestDto);

                assertThat(post)
                    .extracting("isDeleted")
                    .isEqualTo(true);

                verify(postRepository, times(1)).findWithAuthorById(1L);
                verify(userRepository, times(1)).findById(1L);
                verify(commentRepository, times(1)).deleteAllByPost(post);
            }
        }
    }
}
