package com.spring.blog.comment.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.spring.blog.comment.application.CommentService;
import com.spring.blog.comment.application.dto.CommentEditRequestDto;
import com.spring.blog.comment.application.dto.CommentListRequestDto;
import com.spring.blog.comment.application.dto.CommentListResponseDto;
import com.spring.blog.comment.application.dto.CommentReplyRequestDto;
import com.spring.blog.comment.application.dto.CommentResponseDto;
import com.spring.blog.comment.application.dto.CommentWriteRequestDto;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.common.DatabaseCleaner;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import com.spring.blog.exception.comment.CannotEditCommentException;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("CommentService 통합 테스트")
@ActiveProfiles("test")
@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    /*
     Comment1
        ㄴ child1
             ㄴ child3
             ㄴ child4
         ㄴ child2
       Comment2
       Comment3
         ㄴ child5 (페이지네이션으로 인해 조회되지 않음)
     */
    @DisplayName("계층형 댓글을 그룹, 생성일, 계층 오름차순으로 정렬 및 페이징해 조회한다.")
    @Test
    void readCommentList_Pagination_True() {
        // given
        User user = new User("kevin", "image");
        Post post = new Post("hi", "there", user);
        userRepository.save(user);
        postRepository.save(post);

        Comment comment1 = new Comment("1", post, user);
        comment1.updateAsRoot();
        Comment comment2 = new Comment("2", post, user);
        comment2.updateAsRoot();
        Comment comment3 = new Comment("3", post, user);
        comment3.updateAsRoot();
        commentRepository.saveAll(Arrays.asList(comment1, comment2, comment3));

        Long child1Id = replyComment("c1", comment1.getId(), post, user);
        replyComment("c2", comment1.getId(), post, user);
        replyComment("c3", child1Id, post, user);
        replyComment("c4", child1Id, post, user);
        replyComment("c5", comment3.getId(), post, user);

        CommentListRequestDto commentListRequestDto =
            new CommentListRequestDto(post.getId(), 0L, 7L, 5L);

        // when
        CommentListResponseDto commentListResponseDto =
            commentService.readCommentList(commentListRequestDto);

        // then
        assertThat(commentListResponseDto.getCommentResponseDtos())
            .extracting("content")
            .containsExactly("1", "c1", "c3", "c4", "c2", "2", "3");
    }

    private Long replyComment(String content, Long parentId, Post post, User user) {
        CommentReplyRequestDto commentReplyRequestDto =
            new CommentReplyRequestDto(post.getId(), user.getId(), parentId, content);
        return commentService.replyComment(commentReplyRequestDto).getId();
    }

    @DisplayName("Comment를 특정 Post에 작성한다.")
    @Test
    void writeComment_ToPost_Success() {
        // given
        User user = new User("kevin", "image");
        Post post = new Post("hi", "there", user);
        userRepository.save(user);
        postRepository.save(post);
        CommentWriteRequestDto commentWriteRequestDto =
            new CommentWriteRequestDto(post.getId(), user.getId(), "good");
        CommentResponseDto expected = new CommentResponseDto(null, "kevin", "good", 1L, null);

        // when
        CommentResponseDto commentResponseDto = commentService.writeComment(commentWriteRequestDto);

        // then
        assertThat(commentResponseDto)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdDate")
            .isEqualTo(expected);
    }

    @DisplayName("특정 Post의 Comment에 댓글을 작성한다.")
    @Test
    void writeComment_ToComment_Success() {
        // given
        User user = new User("kevin", "image");
        Post post = new Post("hi", "there", user);
        Comment comment = new Comment("hi", post, user);
        comment.updateAsRoot();
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);
        CommentReplyRequestDto commentReplyRequestDto =
            new CommentReplyRequestDto(post.getId(), user.getId(), comment.getId(), "good");
        CommentResponseDto expected = new CommentResponseDto(null, "kevin", "good", 2L, null);

        // when
        CommentResponseDto commentResponseDto = commentService.replyComment(commentReplyRequestDto);

        // then
        assertThat(commentResponseDto)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdDate")
            .isEqualTo(expected);
    }

    @DisplayName("특정 Comment의 내용 수정한다.")
    @Test
    void editComment_ContentChanged_Success() {
        // given
        User user = new User("kevin", "image");
        Post post = new Post("hi", "there", user);
        Comment comment = new Comment("hi", post, user);
        comment.updateAsRoot();
        userRepository.save(user);
        postRepository.save(post);
        commentRepository.save(comment);
        CommentEditRequestDto commentEditRequestDto =
            new CommentEditRequestDto(comment.getId(), user.getId(), "edit comment");

        // when
        CommentResponseDto commentResponseDto = commentService.editComment(commentEditRequestDto);

        // then
        assertThat(commentResponseDto)
            .extracting("content")
            .isEqualTo("edit comment");
    }

    @DisplayName("타인이 작성한 Comment 수정에 실패한다.")
    @Test
    void editComment_OtherUserComment_Failure() {
        // given
        User user = new User("kevin", "image");
        User other = new User("foo", "image");
        Post post = new Post("hi", "there", user);
        Comment comment = new Comment("hi", post, user);
        comment.updateAsRoot();
        userRepository.saveAll(Arrays.asList(user, other));
        postRepository.save(post);
        commentRepository.save(comment);
        CommentEditRequestDto commentEditRequestDto =
            new CommentEditRequestDto(comment.getId(), other.getId(), "edit comment");

        // when, then
        assertThatCode(() -> commentService.editComment(commentEditRequestDto))
            .isInstanceOf(CannotEditCommentException.class)
            .hasMessage("댓글을 수정할 수 없습니다.")
            .hasFieldOrPropertyWithValue("errorCode", "C0004")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.BAD_REQUEST);
    }
}
