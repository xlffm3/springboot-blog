package com.spring.blog.post.presentation;

import static com.spring.blog.common.ApiDocumentUtils.getDocumentRequest;
import static com.spring.blog.common.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.blog.authentication.application.AuthService;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.common.PageMaker;
import com.spring.blog.post.application.PostService;
import com.spring.blog.post.application.dto.request.PostDeleteRequestDto;
import com.spring.blog.post.application.dto.request.PostListRequestDto;
import com.spring.blog.post.application.dto.request.PostWriteRequestDto;
import com.spring.blog.post.application.dto.response.PostListResponseDto;
import com.spring.blog.post.application.dto.response.PostResponseDto;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.presentation.dto.response.PostListResponse;
import com.spring.blog.post.presentation.dto.response.PostResponse;
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

@DisplayName("PostController ???????????? ?????????")
@AutoConfigureRestDocs
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private AuthService authService;

    @DisplayName("???????????? ????????? ????????? ????????? ???????????????.")
    @Test
    void write_NotLoginUser_ExceptionThrown() throws Exception {
        // given
        given(authService.validateToken(any())).willReturn(false);

        // when, then
        ResultActions resultActions = mockMvc.perform(multipart("/api/posts"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value("A0001"));

        verify(authService, times(1)).validateToken(any());

        // restDocs
        resultActions.andDo(document("post-write-not-login",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("errorCode").description("?????? ??????")))
        );
    }

    @DisplayName("????????? ????????? ???????????? ????????? ??? ??????.")
    @Test
    void write_LoginUser_Success() throws Exception {
        // given
        String token = "Bearer token";
        AppUser appUser = new LoginUser(1L, "kevin");
        PostResponseDto postResponseDto = PostResponseDto.builder()
            .id(1L)
            .title("title")
            .content("content")
            .imageUrls(Arrays.asList("url1", "url2"))
            .author("kevin")
            .viewCounts(0L)
            .build();

        given(authService.validateToken("token")).willReturn(true);
        given(authService.findRequestUserByToken("token")).willReturn(appUser);
        given(postService.write(any(PostWriteRequestDto.class))).willReturn(postResponseDto);

        // when, then
        ResultActions resultActions = mockMvc.perform(multipart("/api/posts")
            .file("images", "test1".getBytes())
            .file("images", "test1".getBytes())
            .param("title", "title")
            .param("content", "content")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated())
            .andExpect(header().string("location", "/api/posts/1"));

        verify(authService, times(1)).validateToken("token");
        verify(authService, times(1)).findRequestUserByToken("token");
        verify(postService, times(1)).write(any(PostWriteRequestDto.class));

        // restDocs
        resultActions.andDo(document("post-write-login",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")),
            requestPartBody("images"),
            responseHeaders(headerWithName(HttpHeaders.LOCATION).description("????????? ??????")))
        );
    }

    @DisplayName("???????????? ?????? ????????????.")
    @Test
    void read_SinglePost_Success() throws Exception {
        // given
        PostResponseDto postResponseDto = PostResponseDto.builder()
            .id(1L)
            .title("title")
            .content("content")
            .imageUrls(Arrays.asList("url1", "url2"))
            .author("kevin")
            .viewCounts(0L)
            .createdDate(LocalDateTime.now())
            .modifiedDate(LocalDateTime.now())
            .build();
        PostResponse postResponse = PostResponse.from(postResponseDto);
        String expected = objectMapper.writeValueAsString(postResponse);
        given(postService.readById(1L)).willReturn(postResponseDto);

        // when, then
        ResultActions resultActions = mockMvc.perform(get("/api/posts/{id}", "1")
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().string(expected));

        verify(postService, times(1)).readById(1L);

        // restDocs
        resultActions.andDo(document("post-read-one",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("id").description("????????? id"),
                fieldWithPath("title").description("??????"),
                fieldWithPath("content").description("??????"),
                fieldWithPath("author").description("?????????"),
                fieldWithPath("viewCounts").description("?????????"),
                fieldWithPath("createdDate").description("?????????"),
                fieldWithPath("modifiedDate").description("?????????"),
                fieldWithPath("imageUrls").description("?????? ??????")))
        );
    }

    @DisplayName("????????? ????????? ????????? ???????????????????????? ????????? ????????????.")
    @Test
    void readList_OrderByDateDesc_Success() throws Exception {
        // given
        List<Post> mockPosts = Arrays.asList(
            new Post(1L, "a3", "b3", new User("kevin3", "image")),
            new Post(2L, "a2", "b2", new User("kevin2", "image")),
            new Post(3L, "a", "b", new User("kevin", "image"))
        );
        PageMaker pageMaker = new PageMaker(0, 3, 5, 3);
        PostListResponseDto postListResponseDto = PostListResponseDto.from(mockPosts, pageMaker);
        PostListResponse postListResponse = PostListResponse.from(postListResponseDto);
        String expectedResponseBody = objectMapper.writeValueAsString(postListResponse);
        given(postService.readPostList(any(PostListRequestDto.class)))
            .willReturn(postListResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
            get("/api/posts?page={page}&size={size}&pageBlockCounts={block}", "0", "3", "5"))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponseBody));

        // then
        verify(postService, times(1)).readPostList(any(PostListRequestDto.class));

        // restDocs
        resultActions.andDo(document("post-read-multiple",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("simplePostResponses[].id").description("????????? id"),
                fieldWithPath("simplePostResponses[].title").description("??????"),
                fieldWithPath("simplePostResponses[].content").description("??????"),
                fieldWithPath("simplePostResponses[].author").description("?????????"),
                fieldWithPath("simplePostResponses[].viewCounts").description("?????????"),
                fieldWithPath("simplePostResponses[].createdDate").description("?????????"),
                fieldWithPath("simplePostResponses[].modifiedDate").description("?????????"),
                fieldWithPath("startPage").description("?????? ?????????"),
                fieldWithPath("endPage").description("??? ?????????"),
                fieldWithPath("prev").description("?????? ????????? ??????"),
                fieldWithPath("next").description("?????? ????????? ??????")))
        );
    }

    @DisplayName("????????? ????????? ????????? ?????? ????????? ????????? ???????????????????????? ????????? ????????????.")
    @Test
    void readList_SearchCondition_Success() throws Exception {
        // given
        List<Post> mockPosts = Arrays.asList(
            new Post(1L, "a3", "b3", new User("kevin3", "image")),
            new Post(2L, "a2", "b2", new User("kevin2", "image")),
            new Post(3L, "a", "b", new User("kevin", "image"))
        );
        PageMaker pageMaker = new PageMaker(0, 3, 5, 3);
        PostListResponseDto postListResponseDto = PostListResponseDto.from(mockPosts, pageMaker);
        PostListResponse postListResponse = PostListResponse.from(postListResponseDto);
        String expectedResponseBody = objectMapper.writeValueAsString(postListResponse);
        given(postService.readPostList(any(PostListRequestDto.class)))
            .willReturn(postListResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
            get("/api/posts?page={page}&size={size}&pageBlockCounts={block}&searchType={searchType}&keyword={keyword}",
                "0", "3", "5", "content", "b"))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponseBody));

        // then
        verify(postService, times(1)).readPostList(any(PostListRequestDto.class));

        // restDocs
        resultActions.andDo(document("post-read-multiple-with-search-filter",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("simplePostResponses[].id").description("????????? id"),
                fieldWithPath("simplePostResponses[].title").description("??????"),
                fieldWithPath("simplePostResponses[].content").description("??????"),
                fieldWithPath("simplePostResponses[].author").description("?????????"),
                fieldWithPath("simplePostResponses[].viewCounts").description("?????????"),
                fieldWithPath("simplePostResponses[].createdDate").description("?????????"),
                fieldWithPath("simplePostResponses[].modifiedDate").description("?????????"),
                fieldWithPath("startPage").description("?????? ?????????"),
                fieldWithPath("endPage").description("??? ?????????"),
                fieldWithPath("prev").description("?????? ????????? ??????"),
                fieldWithPath("next").description("?????? ????????? ??????")))
        );
    }

    @DisplayName("???????????? ????????? ????????? ????????? ???????????????.")
    @Test
    void delete_GuestUser_ExceptionThrown() throws Exception {
        // given
        given(authService.validateToken(any())).willReturn(false);

        // when, then
        ResultActions resultActions = mockMvc.perform(delete("/api/posts/1"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value("A0001"));

        verify(authService, times(1)).validateToken(any());

        // restDocs
        resultActions.andDo(document("post-delete-not-login",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(fieldWithPath("errorCode").description("?????? ??????")))
        );
    }

    @DisplayName("????????? ????????? ????????? ????????? ????????????.")
    @Test
    void delete_LoginUser_ExceptionThrown() throws Exception {
        // given
        given(authService.validateToken("token")).willReturn(true);
        given(authService.findRequestUserByToken("token"))
            .willReturn(new LoginUser(1L, "kevin"));
        Mockito.doNothing().when(postService).deletePost(any(PostDeleteRequestDto.class));

        // when, then
        ResultActions resultActions = mockMvc.perform(delete("/api/posts/1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(status().isNoContent());

        verify(authService, times(1)).validateToken("token");
        verify(authService, times(1)).findRequestUserByToken("token");
        verify(postService, times(1)).deletePost(any(PostDeleteRequestDto.class));

        // restDocs
        resultActions.andDo(document("post-delete-login",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")))
        );
    }
}

