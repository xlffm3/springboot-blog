package com.spring.blog.comment.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.blog.comment.application.CommentService;
import com.spring.blog.comment.application.dto.CommentListRequestDto;
import com.spring.blog.comment.application.dto.CommentListResponseDto;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.domain.repository.CommentRepository;
import com.spring.blog.configuration.InfrastructureTestConfiguration;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.post.domain.repository.PostRepository;
import com.spring.blog.user.domain.User;
import com.spring.blog.user.domain.repoistory.UserRepository;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
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
        Post post = new Post(new PostContent("hi", "there"), user);
        userRepository.save(user);
        postRepository.save(post);

        Comment comment1 = new Comment("1", post, user);
        comment1.updateAsRoot();
        Comment comment2 = new Comment("2", post, user);
        comment2.updateAsRoot();
        Comment comment3 = new Comment("3", post, user);
        comment3.updateAsRoot();
        Comment child1 = new Comment("c1", post, user);
        Comment child2 = new Comment("c2", post, user);
        Comment child3 = new Comment("c3", post, user);
        Comment child4 = new Comment("c4", post, user);
        Comment child5 = new Comment("c5", post, user);
        comment1.addChildComment(child1);
        comment1.addChildComment(child2);
        child1.addChildComment(child3);
        child1.addChildComment(child4);
        comment3.addChildComment(child5);
        commentRepository.saveAll(
            Arrays.asList(
                comment1,
                comment2,
                comment3,
                child1,
                child2,
                child3,
                child4,
                child5
            )
        );
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
}
