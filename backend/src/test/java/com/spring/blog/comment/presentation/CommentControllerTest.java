package com.spring.blog.comment.presentation;

import static com.spring.blog.common.ApiDocumentUtils.getDocumentRequest;
import static com.spring.blog.common.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.blog.authentication.application.AuthService;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.comment.application.CommentService;
import com.spring.blog.comment.application.dto.request.CommentDeleteRequestDto;
import com.spring.blog.comment.application.dto.request.CommentEditRequestDto;
import com.spring.blog.comment.application.dto.request.CommentListRequestDto;
import com.spring.blog.comment.application.dto.request.CommentReplyRequestDto;
import com.spring.blog.comment.application.dto.request.CommentWriteRequestDto;
import com.spring.blog.comment.application.dto.response.CommentListResponseDto;
import com.spring.blog.comment.application.dto.response.CommentResponseDto;
import com.spring.blog.comment.domain.Comment;
import com.spring.blog.comment.presentation.dto.request.CommentWriteRequest;
import com.spring.blog.comment.presentation.dto.response.CommentListResponse;
import com.spring.blog.common.PageMaker;
import com.spring.blog.post.domain.Post;
import com.spring.blog.user.domain.User;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("CommentController ???????????? ?????????")
@AutoConfigureRestDocs
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private AuthService authService;

    @DisplayName("???????????? ????????? ????????? ????????? ??? ??????.")
    @Test
    void write_NotLoginUser_Fail() throws Exception {
        // given, when, then
        ResultActions resultActions = mockMvc.perform(post("/api/posts/{postId}/comments", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value("A0001"));

        verify(authService, times(1)).validateToken(any());

        // restDocs
        resultActions.andDo(document("comment-write-not-login",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("errorCode").description("?????? ??????")))
        );
    }

    @DisplayName("????????? ????????? ????????? ????????? ??? ??????.")
    @Test
    void write_LoginUser_Success() throws Exception {
        // given
        String requestBody =
            objectMapper.writeValueAsString(new CommentWriteRequest("comment hi"));
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
            .id(1L)
            .author("kevin")
            .content("comment hi")
            .depth(1L)
            .createdDate(LocalDateTime.now())
            .build();

        given(authService.validateToken("token")).willReturn(true);
        given(authService.findRequestUserByToken("token")).willReturn(new LoginUser(1L, "kevin"));
        given(commentService.writeComment(any(CommentWriteRequestDto.class)))
            .willReturn(commentResponseDto);

        // when, then
        ResultActions resultActions = mockMvc.perform(post("/api/posts/{postId}/comments", "1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE).content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.author").value("kevin"))
            .andExpect(jsonPath("$.content").value("comment hi"))
            .andExpect(jsonPath("$.depth").value("1"));

        verify(authService, times(1)).validateToken("token");
        verify(authService, times(1)).findRequestUserByToken("token");
        verify(commentService, times(1))
            .writeComment(any(CommentWriteRequestDto.class));

        // restDocs
        resultActions.andDo(document("comment-write-login",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")),
            responseFields(fieldWithPath("id").description("?????? id"),
                fieldWithPath("author").description("?????????"),
                fieldWithPath("content").description("?????? ??????"),
                fieldWithPath("depth").description("?????? ?????? ??????"),
                fieldWithPath("createdDate").description("?????????")))
        );
    }

    @DisplayName("???????????? ????????? ???????????? ????????? ??? ??????.")
    @Test
    void reply_NotLoginUser_Fail() throws Exception {
        // given
        given(authService.validateToken(any())).willReturn(false);

        // when, then
        ResultActions resultActions = mockMvc
            .perform(post("/api/posts/{postId}/comments/{commentId}/reply", "1", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value("A0001"));

        verify(authService, times(1)).validateToken(any());

        // restDocs
        resultActions.andDo(document("comment-reply-not-login",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("errorCode").description("?????? ??????")))
        );
    }

    @DisplayName("????????? ????????? ???????????? ????????? ??? ??????.")
    @Test
    void reply_LoginUser_Success() throws Exception {
        // given
        String requestBody =
            objectMapper.writeValueAsString(new CommentWriteRequest("comment hi"));
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
            .id(2L)
            .author("kevin")
            .content("comment hi")
            .depth(2L)
            .createdDate(LocalDateTime.now())
            .build();

        given(authService.validateToken("token")).willReturn(true);
        given(authService.findRequestUserByToken("token")).willReturn(new LoginUser(1L, "kevin"));
        given(commentService.replyComment(any(CommentReplyRequestDto.class)))
            .willReturn(commentResponseDto);

        // when, then
        ResultActions resultActions = mockMvc
            .perform(post("/api/posts/{postId}/comments/{commentId}/reply", "1", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("2"))
            .andExpect(jsonPath("$.author").value("kevin"))
            .andExpect(jsonPath("$.content").value("comment hi"))
            .andExpect(jsonPath("$.depth").value("2"));

        verify(authService, times(1)).validateToken("token");
        verify(authService, times(1)).findRequestUserByToken("token");
        verify(commentService, times(1))
            .replyComment(any(CommentReplyRequestDto.class));

        // restDocs
        resultActions.andDo(document("comment-reply-login",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")),
            responseFields(fieldWithPath("id").description("?????? id"),
                fieldWithPath("author").description("?????????"),
                fieldWithPath("content").description("?????? ??????"),
                fieldWithPath("depth").description("?????? ?????? ??????"),
                fieldWithPath("createdDate").description("?????????")))
        );
    }

    @DisplayName("???????????? ????????? ????????? ????????? ??? ??????.")
    @Test
    void edit_GuestUser_Failure() throws Exception {
        // given
        String requestBody =
            objectMapper.writeValueAsString(new CommentWriteRequest("comment hi"));
        given(authService.validateToken(any())).willReturn(false);

        // when, then
        ResultActions resultActions = mockMvc.perform(put("/api/comments/{commentId}", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(requestBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value("A0001"));

        verify(authService, times(1)).validateToken(any());

        // restDocs
        resultActions.andDo(document("comment-edit-not-login",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("errorCode").description("?????? ??????")))
        );
    }

    @DisplayName("????????? ????????? ????????? ????????? ??? ??????.")
    @Test
    void edit_LoginUser_Success() throws Exception {
        // given
        String requestBody =
            objectMapper.writeValueAsString(new CommentWriteRequest("comment hi"));
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
            .id(2L)
            .author("kevin")
            .content("comment hi")
            .depth(2L)
            .createdDate(LocalDateTime.now())
            .build();

        given(authService.validateToken("token")).willReturn(true);
        given(authService.findRequestUserByToken("token")).willReturn(new LoginUser(1L, "kevin"));
        given(commentService.editComment(any(CommentEditRequestDto.class)))
            .willReturn(commentResponseDto);

        // when, then
        ResultActions resultActions = mockMvc.perform(put("/api/comments/{commentId}", "1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("2"))
            .andExpect(jsonPath("$.author").value("kevin"))
            .andExpect(jsonPath("$.content").value("comment hi"))
            .andExpect(jsonPath("$.depth").value("2"));

        verify(authService, times(1)).validateToken("token");
        verify(authService, times(1)).findRequestUserByToken("token");
        verify(commentService, times(1))
            .editComment(any(CommentEditRequestDto.class));

        // restDocs
        resultActions.andDo(document("comment-edit-login",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")),
            responseFields(fieldWithPath("id").description("?????? id"),
                fieldWithPath("author").description("?????????"),
                fieldWithPath("content").description("?????? ??????"),
                fieldWithPath("depth").description("?????? ?????? ??????"),
                fieldWithPath("createdDate").description("?????????")))
        );
    }

    @DisplayName("???????????? ????????? ????????? ????????? ??? ??????.")
    @Test
    void delete_GuestUser_Failure() throws Exception {
        // given
        given(authService.validateToken(any())).willReturn(false);

        // when, then
        ResultActions resultActions = mockMvc.perform(delete("/api/comments/{commentId}", "1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value("A0001"));

        verify(authService, times(1)).validateToken(any());

        // restDocs
        resultActions.andDo(document("comment-delete-not-login",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("errorCode").description("?????? ??????")))
        );
    }

    @DisplayName("????????? ????????? ????????? ????????? ??? ??????.")
    @Test
    void delete_GuestUser_Success() throws Exception {
        // given
        given(authService.validateToken("token")).willReturn(true);
        given(authService.findRequestUserByToken("token")).willReturn(new LoginUser(1L, "kevin"));
        Mockito.doNothing().when(commentService).deleteComment(any(CommentDeleteRequestDto.class));

        // when, then
        ResultActions resultActions = mockMvc.perform(delete("/api/comments/{commentId}", "1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(status().isNoContent());

        verify(authService, times(1)).validateToken("token");
        verify(authService, times(1)).findRequestUserByToken("token");
        verify(commentService, times(1))
            .deleteComment(any(CommentDeleteRequestDto.class));

        // restDocs
        resultActions.andDo(document("comment-delete-login",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")))
        );
    }

    @DisplayName("???????????? ?????? Comment??? ???????????????????????? ????????????.")
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
        ResultActions resultActions = mockMvc.perform(
            get("/api/posts/{postId}/comments?page={page}&size={size}&pageBlockCounts={block}",
                "1", "1", "5", "10")
                .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(status().isOk())
            .andExpect(content().string(expectedBody));

        verify(commentService, times(1)).readCommentList(any(CommentListRequestDto.class));

        // restDocs
        resultActions.andDo(document("comment-list-read",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("commentResponses[].id").description("????????? id"),
                fieldWithPath("commentResponses[].content").description("??????"),
                fieldWithPath("commentResponses[].author").description("?????????"),
                fieldWithPath("commentResponses[].depth").description("?????? ?????? ??????"),
                fieldWithPath("commentResponses[].createdDate").description("?????????"),
                fieldWithPath("startPage").description("?????? ?????????"),
                fieldWithPath("endPage").description("??? ?????????"),
                fieldWithPath("prev").description("?????? ????????? ??????"),
                fieldWithPath("next").description("?????? ????????? ??????")))
        );
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
        comment1.updateChildCommentHierarchy(child1);
        comment1.updateChildCommentHierarchy(child2);
        child1.updateChildCommentHierarchy(child3);
        child1.updateChildCommentHierarchy(child4);
        comment3.updateChildCommentHierarchy(child5);
        return Arrays.asList(comment1, comment2, comment3, child1, child2, child3, child4, child5);
    }
}
