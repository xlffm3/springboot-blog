package com.spring.blog.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.post.application.dto.PostRequestDto;
import com.spring.blog.post.application.dto.PostResponseDto;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@DisplayName("PostService 슬라이스 테스트")
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

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
                PostRequestDto postRequestDto = new PostRequestDto(1L, "title", "content");
                given(userRepository.findById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> postService.write(postRequestDto))
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
                PostRequestDto postRequestDto = new PostRequestDto(1L, "title", "content");
                User user = new User("kevin", "image");
                Post post = new Post(1L, new PostContent("title", "content"), user);
                given(userRepository.findById(1L)).willReturn(Optional.of(user));
                given(postRepository.save(any(Post.class))).willReturn(post);

                // when
                PostResponseDto postResponseDto = postService.write(postRequestDto);

                assertThat(postResponseDto)
                    .extracting("id", "title", "content", "author", "viewCounts")
                    .containsExactly(1L, "title", "content", "kevin", 0L);

                verify(userRepository, times(1)).findById(1L);
                verify(postRepository, times(1)).save(any(Post.class));
            }
        }
    }
}
