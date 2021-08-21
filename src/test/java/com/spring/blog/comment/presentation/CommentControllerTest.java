package com.spring.blog.comment.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.comment.application.CommentService;
import com.spring.blog.comment.application.dto.CommentListRequestDto;
import com.spring.blog.comment.application.dto.CommentListResponseDto;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.presentation.dto.CommentListResponse;
import com.spring.blog.common.PageMaker;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.domain.content.PostContent;
import com.spring.blog.user.domain.User;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("CommentController 슬라이스 테스트")
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private OAuthService oAuthService;

    @DisplayName("게시물에 달린 Comment를 페이지네이션으로 조회한다.")
    @Test
    void readList_Pagination_Success() throws Exception {
        // given
        List<Comment> comments = generateComments();
        PageMaker pageMaker = new PageMaker(1, 5, 10, 30);
        CommentListResponseDto commentListResponseDto =
            CommentListResponseDto.from(comments, pageMaker);
        CommentListRequestDto commentListRequestDto =
            new CommentListRequestDto(1L, 1L, 5L, 10L);
        CommentListResponse commentListResponse =
            CommentListResponse.from(commentListResponseDto);
        String expectedBody = objectMapper.writeValueAsString(commentListResponse);
        given(commentService.readCommentList(any(CommentListRequestDto.class)))
            .willReturn(commentListResponseDto);

        // when, then
        mockMvc.perform(
            get("/api/comments/{postId}?page={page}&size={size}&pageBlockCounts={block}",
                "1", "1", "5", "10")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isOk())
            .andExpect(content().string(expectedBody));

        verify(commentService, times(1)).readCommentList(any(CommentListRequestDto.class));
    }

    private List<Comment> generateComments() {
        User user = new User(1L, "kevin", "image");
        Post post = new Post(1L, new PostContent("title", "content"), user);

        Comment comment1 = new Comment(1L, "1", post, user);
        comment1.updateAsRoot();
        Comment comment2 = new Comment(2L, "2", post, user);
        comment2.updateAsRoot();
        Comment comment3 = new Comment(3L, "3", post, user);
        comment3.updateAsRoot();
        Comment child1 = new Comment(4L, "c1", post, user);
        Comment child2 = new Comment(5L, "c2", post, user);
        Comment child3 = new Comment(6L, "c3", post, user);
        Comment child4 = new Comment(7L, "c4", post, user);
        Comment child5 = new Comment(8L, "c5", post, user);
        comment1.addChildComment(child1);
        comment1.addChildComment(child2);
        child1.addChildComment(child3);
        child1.addChildComment(child4);
        comment3.addChildComment(child5);
        return Arrays.asList(
            comment1,
            comment2,
            comment3,
            child1,
            child2,
            child3,
            child4,
            child5
        );
    }
}
