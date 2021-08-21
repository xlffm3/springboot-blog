package com.spring.blog.comment.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.comment.application.CommentService;
import com.spring.blog.comment.application.dto.CommentListRequestDto;
import com.spring.blog.comment.application.dto.CommentListResponseDto;
import com.spring.blog.comment.application.dto.CommentResponseDto;
import com.spring.blog.comment.application.dto.CommentWriteRequestDto;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.presentation.dto.CommentListResponse;
import com.spring.blog.comment.presentation.dto.CommentWriteRequest;
import com.spring.blog.common.PageMaker;
import com.spring.blog.post.domain.Post;
import com.spring.blog.user.domain.User;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
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

    @DisplayName("비로그인 유저는 댓글을 작성할 수 없다.")
    @Test
    void write_NotLoginUser_Fail() throws Exception {
        // given
        CommentWriteRequest commentWriteRequest = new CommentWriteRequest("comment hi");
        given(oAuthService.validateToken(any())).willReturn(false);

        // when, then
        mockMvc.perform(post("/api/posts/{postId}/comments", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isUnauthorized());

        verify(oAuthService, times(1)).validateToken(any());
    }

    @DisplayName("로그인 유저는 댓글을 작성할 수 있다.")
    @Test
    void write_LoginUser_Success() throws Exception {
        // given
        String requestBody =
            objectMapper.writeValueAsString(new CommentWriteRequest("comment hi"));
        CommentResponseDto commentResponseDto =
            new CommentResponseDto(1L, "kevin", "comment hi", 1, LocalDateTime.now());

        given(oAuthService.validateToken("token")).willReturn(true);
        given(oAuthService.findRequestUserByToken("token")).willReturn(new LoginUser(1L, "kevin"));
        given(commentService.writeComment(any(CommentWriteRequestDto.class)))
            .willReturn(commentResponseDto);

        // when, then
        mockMvc.perform(post("/api/posts/{postId}/comments", "1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.author").value("kevin"))
            .andExpect(jsonPath("$.content").value("comment hi"))
            .andExpect(jsonPath("$.depth").value("1"));

        verify(oAuthService, times(1)).validateToken("token");
        verify(oAuthService, times(1)).findRequestUserByToken("token");
        verify(commentService, times(1))
            .writeComment(any(CommentWriteRequestDto.class));
    }

    @DisplayName("게시물에 달린 Comment를 페이지네이션으로 조회한다.")
    @Test
    void readList_Pagination_Success() throws Exception {
        // given
        List<Comment> comments = generateComments();
        PageMaker pageMaker = new PageMaker(1, 5, 10, 30);
        CommentListResponseDto commentListResponseDto =
            CommentListResponseDto.from(comments, pageMaker);
        CommentListResponse commentListResponse =
            CommentListResponse.from(commentListResponseDto);
        String expectedBody = objectMapper.writeValueAsString(commentListResponse);
        given(commentService.readCommentList(any(CommentListRequestDto.class)))
            .willReturn(commentListResponseDto);

        // when, then
        mockMvc.perform(
            get("/api/posts/{postId}/comments?page={page}&size={size}&pageBlockCounts={block}",
                "1", "1", "5", "10")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isOk())
            .andExpect(content().string(expectedBody));

        verify(commentService, times(1)).readCommentList(any(CommentListRequestDto.class));
    }

    private List<Comment> generateComments() {
        User user = new User(1L, "kevin", "image");
        Post post = new Post(1L, "title", "content", user);

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
        return Arrays.asList(comment1, comment2, comment3, child1, child2, child3, child4, child5);
    }
}
