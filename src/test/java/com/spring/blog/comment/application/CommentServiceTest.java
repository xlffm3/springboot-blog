package com.spring.blog.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spring.blog.comment.application.dto.CommentListRequestDto;
import com.spring.blog.comment.application.dto.CommentListResponseDto;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@DisplayName("CommentService 슬라이스 테스트")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @DisplayName("readCommentList 메서드는")
    @Nested
    class Describe_readCommentList {

        @DisplayName("Id에 해당하는 Post가 존재하지 않을 때")
        @Nested
        class Context_post_not_found {

            @DisplayName("예외를 발생시킨다.")
            @Test
            void it_throws_PostNotFoundException() {
                // given
                CommentListRequestDto commentListRequestDto =
                    new CommentListRequestDto(1L, 1L, 3L, 5L);
                given(postRepository.findById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.readCommentList(commentListRequestDto))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("게시글을 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(postRepository, times(1)).findById(1L);
            }
        }

        @DisplayName("ID에 해당하는 Post가 존재하는 경우")
        @Nested
        class Context_post_id_found {

            @DisplayName("댓글들을 그룹, 생성일, 계층 오름차순으로 정렬해 반환한다.")
            @Test
            void it_returns_comments_order_by_group_date_hierarchy() {
                // given
                User user = new User(1L, "kevin", "image");
                Post post = new Post(1L, "title", "content", user);
                CommentListRequestDto commentListRequestDto =
                    new CommentListRequestDto(1L, 1L, 3L, 5L);
                Comment comment1 = new Comment("1", post, user);
                comment1.updateAsRoot();
                Comment comment2 = new Comment("2", post, user);
                comment2.updateAsRoot();
                Comment comment3 = new Comment("3", post, user);
                comment3.updateAsRoot();

                given(postRepository.findById(1L)).willReturn(Optional.of(post));
                given(commentRepository.findCommentsOrderByHierarchyAndDateDesc(any(Pageable.class), eq(post)))
                    .willReturn(Arrays.asList(comment1, comment2, comment3));
                given(commentRepository.countCommentByPost(eq(post))).willReturn(15L);

                // when
                CommentListResponseDto commentListResponseDto =
                    commentService.readCommentList(commentListRequestDto);

                // then
                assertThat(commentListResponseDto)
                    .extracting("startPage", "endPage", "prev", "next")
                    .containsExactly(1, 5, false, false);
                assertThat(commentListResponseDto.getCommentResponseDtos())
                    .extracting("author", "content")
                    .containsExactly(
                        tuple("kevin", "1"),
                        tuple("kevin", "2"),
                        tuple("kevin", "3")
                    );

                verify(postRepository, times(1)).findById(1L);
                verify(commentRepository, times(1))
                    .findCommentsOrderByHierarchyAndDateDesc(any(Pageable.class), eq(post));
                verify(commentRepository, times(1))
                    .countCommentByPost(eq(post));
            }
        }
    }
}
