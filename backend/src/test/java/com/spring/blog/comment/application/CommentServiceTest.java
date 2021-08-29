package com.spring.blog.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.spring.blog.comment.application.dto.request.CommentDeleteRequestDto;
import com.spring.blog.comment.application.dto.request.CommentEditRequestDto;
import com.spring.blog.comment.application.dto.request.CommentListRequestDto;
import com.spring.blog.comment.application.dto.response.CommentListResponseDto;
import com.spring.blog.comment.application.dto.request.CommentReplyRequestDto;
import com.spring.blog.comment.application.dto.response.CommentResponseDto;
import com.spring.blog.comment.application.dto.request.CommentWriteRequestDto;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.exception.comment.CommentNotFoundException;
import com.spring.blog.exception.post.PostNotFoundException;
import com.spring.blog.exception.user.UserNotFoundException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
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

    @Mock
    private UserRepository userRepository;

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
                CommentListRequestDto commentListRequestDto = CommentListRequestDto.builder()
                    .postId(1L)
                    .page(1L)
                    .size(3L)
                    .pageBlockCounts(5L)
                    .build();
                given(postRepository.findActivePostById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.readCommentList(commentListRequestDto))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("게시글을 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(postRepository, times(1)).findActivePostById(1L);
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
                CommentListRequestDto commentListRequestDto = CommentListRequestDto.builder()
                    .postId(1L)
                    .page(1L)
                    .size(3L)
                    .pageBlockCounts(5L)
                    .build();
                Comment comment1 = new Comment("1", post, user);
                comment1.updateAsRoot();
                Comment comment2 = new Comment("2", post, user);
                comment2.updateAsRoot();
                Comment comment3 = new Comment("3", post, user);
                comment3.updateAsRoot();

                given(postRepository.findActivePostById(1L)).willReturn(Optional.of(post));
                given(commentRepository
                    .findCommentsOrderByHierarchy(any(Pageable.class), eq(post)))
                    .willReturn(Arrays.asList(comment1, comment2, comment3));
                given(commentRepository.countCommentsByPost(eq(post))).willReturn(15L);

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

                verify(postRepository, times(1)).findActivePostById(1L);
                verify(commentRepository, times(1))
                    .findCommentsOrderByHierarchy(any(Pageable.class), eq(post));
                verify(commentRepository, times(1))
                    .countCommentsByPost(eq(post));
            }
        }
    }

    @DisplayName("writeComment 메서드는")
    @Nested
    class Describe_writeComment {

        @DisplayName("글을 작성할 유저가 존재하지 않으면")
        @Nested
        class Context_user_not_found {

            @DisplayName("유저 조회 불가 예외가 발생한다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                CommentWriteRequestDto commentWriteRequestDto = CommentWriteRequestDto.builder()
                    .postId(1L)
                    .userId(1L)
                    .content("kevin")
                    .build();
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.writeComment(commentWriteRequestDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("유저를 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(userRepository, times(1)).findActiveUserById(1L);
            }
        }

        @DisplayName("댓글을 작성할 게시글이 존재하지 않으면")
        @Nested
        class Context_comment_not_found {

            @DisplayName("게시글 조회 불가 예외가 발생한다.")
            @Test
            void it_throws_PostNotFoundException() {
                // given
                CommentWriteRequestDto commentWriteRequestDto = CommentWriteRequestDto.builder()
                    .postId(1L)
                    .userId(1L)
                    .content("kevin")
                    .build();
                User user = new User(1L, "kevin", "image");
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.of(user));
                given(postRepository.findActivePostById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.writeComment(commentWriteRequestDto))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("게시글을 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(userRepository, times(1)).findActiveUserById(1L);
                verify(postRepository, times(1)).findActivePostById(1L);
            }
        }

        @DisplayName("유저와 게시물이 존재하면")
        @Nested
        class Context_comment_user_found {

            @DisplayName("댓글이 저장된다.")
            @Test
            void it_saves_comment() {
                // given
                CommentWriteRequestDto commentWriteRequestDto = CommentWriteRequestDto.builder()
                    .postId(1L)
                    .userId(1L)
                    .content("kevin")
                    .build();
                User user = new User(1L, "kevin", "image");
                Post post = new Post("title", "content", user);
                Comment comment = new Comment(1L, "comment", post, user);
                CommentResponseDto expected = CommentResponseDto.builder()
                    .id(1L)
                    .author("kevin")
                    .content("comment")
                    .depth(1L)
                    .build();
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.of(user));
                given(postRepository.findActivePostById(1L)).willReturn(Optional.of(post));
                given(commentRepository.save(any(Comment.class))).willReturn(comment);

                // when
                CommentResponseDto commentResponseDto =
                    commentService.writeComment(commentWriteRequestDto);

                // then
                assertThat(commentResponseDto)
                    .usingRecursiveComparison()
                    .ignoringFields("createdDate")
                    .isEqualTo(expected);

                verify(userRepository, times(1)).findActiveUserById(1L);
                verify(postRepository, times(1)).findActivePostById(1L);
                verify(commentRepository, times(1)).save(any(Comment.class));
            }
        }
    }

    @DisplayName("replyComment 메서드는")
    @Nested
    class Describe_replyComment {

        @DisplayName("ID에 해당하는 User가 존재하지 않는다면")
        @Nested
        class Context_user_not_found {

            @DisplayName("유저 조회 불가 예외가 발생한다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                CommentReplyRequestDto commentReplyRequestDto = CommentReplyRequestDto.builder()
                    .postId(1L)
                    .userId(1L)
                    .commentId(1L)
                    .content("ahah")
                    .build();
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.replyComment(commentReplyRequestDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("유저를 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(userRepository, times(1)).findActiveUserById(1L);
            }
        }

        @DisplayName("ID에 해당하는 Post가 존재하지 않는다면")
        @Nested
        class Context_post_not_found {

            @DisplayName("게시글 조회 불가 예외가 발생한다.")
            @Test
            void it_throws_PostNotFoundException() {
                // given
                CommentReplyRequestDto commentReplyRequestDto = CommentReplyRequestDto.builder()
                    .postId(1L)
                    .userId(1L)
                    .commentId(1L)
                    .content("hahaha")
                    .build();
                User user = new User(1L, "kevin", "image");
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.of(user));
                given(postRepository.findActivePostById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.replyComment(commentReplyRequestDto))
                    .isInstanceOf(PostNotFoundException.class)
                    .hasMessage("게시글을 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "P0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(userRepository, times(1)).findActiveUserById(1L);
                verify(postRepository, times(1)).findActivePostById(1L);
            }
        }

        @DisplayName("ID에 해당하는 부모 Comment가 존재하지 않는다면")
        @Nested
        class Context_comment_not_found {

            @DisplayName("댓글 조회 불가 예외가 발생한다.")
            @Test
            void it_throws_CommentNotFoundException() {
                // given
                CommentReplyRequestDto commentReplyRequestDto = CommentReplyRequestDto.builder()
                    .postId(1L)
                    .userId(1L)
                    .commentId(1L)
                    .content("hahaha")
                    .build();
                User user = new User(1L, "kevin", "image");
                Post post = new Post("title", "content", user);

                given(userRepository.findActiveUserById(1L)).willReturn(Optional.of(user));
                given(postRepository.findActivePostById(1L)).willReturn(Optional.of(post));
                given(commentRepository.findByIdWithRootComment(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.replyComment(commentReplyRequestDto))
                    .isInstanceOf(CommentNotFoundException.class)
                    .hasMessage("댓글을 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(userRepository, times(1)).findActiveUserById(1L);
                verify(postRepository, times(1)).findActivePostById(1L);
                verify(commentRepository, times(1)).findByIdWithRootComment(1L);
            }
        }

        @DisplayName("정상적인 요청이라면")
        @Nested
        class Context_valid_request {

            @DisplayName("대댓글을 등록한다.")
            @Test
            void it_saves_child_comment() {
                // given
                CommentReplyRequestDto commentReplyRequestDto = CommentReplyRequestDto.builder()
                    .postId(1L)
                    .userId(1L)
                    .commentId(1L)
                    .content("hahaha")
                    .build();
                User user = new User(1L, "kevin", "image");
                Post post = new Post("title", "content", user);
                Comment parentComment = new Comment(1L, "comment", post, user);
                Comment childComment = new Comment(2L, "hahaha", post, user);
                CommentResponseDto expected = CommentResponseDto.builder()
                    .id(2L)
                    .author("kevin")
                    .content("hahaha")
                    .depth(2L)
                    .build();
                parentComment.updateAsRoot();
                parentComment.updateChildCommentHierarchy(childComment);

                given(userRepository.findActiveUserById(1L)).willReturn(Optional.of(user));
                given(postRepository.findActivePostById(1L)).willReturn(Optional.of(post));
                given(commentRepository.findByIdWithRootComment(1L)).willReturn(Optional.of(parentComment));
                given(commentRepository.save(any(Comment.class))).willReturn(childComment);

                // when, then
                CommentResponseDto commentResponseDto =
                    commentService.replyComment(commentReplyRequestDto);

                assertThat(commentResponseDto)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "createdDate")
                    .isEqualTo(expected);

                verify(userRepository, times(1)).findActiveUserById(1L);
                verify(postRepository, times(1)).findActivePostById(1L);
                verify(commentRepository, times(1)).findByIdWithRootComment(1L);
                verify(commentRepository, times(1)).adjustHierarchyOrders(any(Comment.class));
                verify(commentRepository, times(1)).save(any(Comment.class));
            }
        }
    }

    @DisplayName("editComment 메서드는")
    @Nested
    class Describe_editComment {

        @DisplayName("해당 ID의 comment가 존재하지 않을 때")
        @Nested
        class Context_comment_not_found {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_throws_CommentNotFoundException() {
                // given
                CommentEditRequestDto commentEditRequestDto = CommentEditRequestDto.builder()
                    .commentId(1L)
                    .userId(1L)
                    .content("edit content")
                    .build();
                given(commentRepository.findByIdWithAuthor(1L))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.editComment(commentEditRequestDto))
                    .isInstanceOf(CommentNotFoundException.class)
                    .hasMessage("댓글을 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(commentRepository, times(1)).findByIdWithAuthor(1L);
            }
        }

        @DisplayName("해당 ID의 user가 존재하지 않을 때")
        @Nested
        class Context_user_not_found {

            @DisplayName("예외가 발생한다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                CommentEditRequestDto commentEditRequestDto = CommentEditRequestDto.builder()
                    .commentId(1L)
                    .userId(1L)
                    .content("edit content")
                    .build();
                User user = new User(1L, "kevin", "image");
                Post post = new Post("title", "content", user);
                Comment comment = new Comment("comment", post, user);
                given(commentRepository.findByIdWithAuthor(1L))
                    .willReturn(Optional.of(comment));
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.editComment(commentEditRequestDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("유저를 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(commentRepository, times(1)).findByIdWithAuthor(1L);
                verify(userRepository, times(1)).findActiveUserById(1L);
            }
        }

        @DisplayName("해당 ID의 User와 Comment가 모두 존재할 때")
        @Nested
        class Context_user_comment_found {

            @DisplayName("댓글을 정상적으로 수정한다.")
            @Test
            void it_edits_comment() {
                // given
                CommentEditRequestDto commentEditRequestDto = CommentEditRequestDto.builder()
                    .commentId(1L)
                    .userId(1L)
                    .content("edit content")
                    .build();
                User user = new User(1L, "kevin", "image");
                Post post = new Post("title", "content", user);
                Comment comment = new Comment("comment", post, user);
                given(commentRepository.findByIdWithAuthor(1L))
                    .willReturn(Optional.of(comment));
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.of(user));

                // when
                CommentResponseDto commentResponseDto =
                    commentService.editComment(commentEditRequestDto);

                // then
                assertThat(commentResponseDto)
                    .extracting("content")
                    .isEqualTo("edit content");

                verify(commentRepository, times(1)).findByIdWithAuthor(1L);
                verify(userRepository, times(1)).findActiveUserById(1L);
            }
        }
    }

    @DisplayName("deleteComment 메서드는")
    @Nested
    class Describe_deleteComment {

        @DisplayName("삭제하려는 댓글이 존재하지 않을 때")
        @Nested
        class Context_comment_not_found {

            @DisplayName("예외를 발생시킨다.")
            @Test
            void it_throws_CommentNotFoundException() {
                // given
                CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
                    .commentId(1L)
                    .userId(1L)
                    .build();
                given(commentRepository.findByIdWithRootCommentAndAuthor(1L))
                    .willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.deleteComment(commentDeleteRequestDto))
                    .isInstanceOf(CommentNotFoundException.class)
                    .hasMessage("댓글을 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "C0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(commentRepository, times(1))
                    .findByIdWithRootCommentAndAuthor(1L);
            }
        }

        @DisplayName("유저가 존재하지 않을 때")
        @Nested
        class Context_user_not_found {

            @DisplayName("예외를 발생시킨다.")
            @Test
            void it_throws_UserNotFoundException() {
                // given
                CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
                    .commentId(1L)
                    .userId(1L)
                    .build();
                User user = new User(1L, "kevin", "image");
                Post post = new Post("title", "content", user);
                Comment comment = new Comment("comment", post, user);
                given(commentRepository.findByIdWithRootCommentAndAuthor(1L))
                    .willReturn(Optional.of(comment));
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.empty());

                // when, then
                assertThatCode(() -> commentService.deleteComment(commentDeleteRequestDto))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("유저를 조회할 수 없습니다.")
                    .hasFieldOrPropertyWithValue("errorCode", "U0001")
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);

                verify(commentRepository, times(1))
                    .findByIdWithRootCommentAndAuthor(1L);
                verify(userRepository, times(1)).findActiveUserById(1L);
            }
        }

        @DisplayName("삭제하려는 댓글 및 작성자가 정상적으로 조회되면")
        @Nested
        class Context_comment_and_user_found {

            @DisplayName("댓글이 삭제된다.")
            @Test
            void it_deletes_comment() {
                // given
                CommentDeleteRequestDto commentDeleteRequestDto = CommentDeleteRequestDto.builder()
                    .commentId(1L)
                    .userId(1L)
                    .build();
                User user = new User(1L, "kevin", "image");
                Post post = new Post("title", "content", user);
                Comment comment = new Comment("comment", post, user);
                given(commentRepository.findByIdWithRootCommentAndAuthor(1L))
                    .willReturn(Optional.of(comment));
                given(userRepository.findActiveUserById(1L)).willReturn(Optional.of(user));

                // when
                commentService.deleteComment(commentDeleteRequestDto);

                // then
                assertThat(comment)
                    .extracting("isDeleted")
                    .isEqualTo(true);

                verify(commentRepository, times(1))
                    .findByIdWithRootCommentAndAuthor(1L);
                verify(userRepository, times(1)).findActiveUserById(1L);
            }
        }
    }
}
