package com.spring.blog.post.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.blog.authentication.application.OAuthService;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.authentication.domain.user.LoginUser;
import com.spring.blog.common.FileFactory;
import com.spring.blog.common.PageMaker;
import com.spring.blog.post.application.PostService;
import com.spring.blog.post.application.dto.PostListRequestDto;
import com.spring.blog.post.application.dto.PostListResponseDto;
import com.spring.blog.post.application.dto.PostResponseDto;
import com.spring.blog.post.application.dto.PostWriteRequestDto;
import com.spring.blog.post.domain.Post;
import com.spring.blog.post.presentation.dto.PostListResponse;
import com.spring.blog.post.presentation.dto.PostResponse;
import com.spring.blog.user.domain.User;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("PostController 슬라이스 테스트")
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private OAuthService oAuthService;

    @DisplayName("비로그인 유저는 게시물 작성이 불가능하다.")
    @Test
    void write_NotLoginUser_ExceptionThrown() throws Exception {
        // given
        given(oAuthService.validateToken(any())).willReturn(false);

        // when, then
        mockMvc.perform(multipart("/api/posts"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").value("A0001"));
    }

    @DisplayName("로그인 유저는 게시물을 작성할 수 있다.")
    @Test
    void write_LoginUser_Success() throws Exception {
        // given
        String token = "Bearer token";
        AppUser appUser = new LoginUser(1L, "kevin");
        PostResponseDto postResponseDto =
            new PostResponseDto(1L, "title", "content", Arrays.asList("url1", "url2"), "kevin", 0L, null, null);

        given(oAuthService.validateToken("token")).willReturn(true);
        given(oAuthService.findRequestUserByToken("token")).willReturn(appUser);
        given(postService.write(any(PostWriteRequestDto.class))).willReturn(postResponseDto);

        // when, then
        mockMvc.perform(multipart("/api/posts")
            .file((MockMultipartFile) FileFactory.getTestSuccessImage1())
            .file((MockMultipartFile) FileFactory.getTestSuccessImage2())
            .param("title", "title")
            .param("content", "content")
            .header(HttpHeaders.AUTHORIZATION, token)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated())
            .andDo(print())
            .andExpect(header().string("location", "/api/posts/1"));

        verify(oAuthService, times(1)).validateToken("token");
        verify(oAuthService, times(1)).findRequestUserByToken("token");
        verify(postService, times(1)).write(any(PostWriteRequestDto.class));
    }

    @DisplayName("게시물을 단건 조회한다.")
    @Test
    void read_SinglePost_Success() throws Exception {
        // given
        PostResponseDto postResponseDto =
            new PostResponseDto(1L, "title", "content", Arrays.asList("url1", "url2"), "kevin", 1L, null, null);
        PostResponse postResponse = PostResponse.from(postResponseDto);

        given(postService.readById(1L)).willReturn(postResponseDto);
        String expected = objectMapper.writeValueAsString(postResponse);

        // when, then
        mockMvc.perform(get("/api/posts/{id}", "1")
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().string(expected));

        verify(postService, times(1)).readById(1L);
    }

    @DisplayName("복수의 게시물 목록을 페이지네이션으로 조회한다.")
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
        mockMvc.perform(get("/api/posts?page={page}&size={size}&pageBlockCounts={block}", "0", "3", "5"))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponseBody));

        // then
        verify(postService, times(1)).readPostList(any(PostListRequestDto.class));
    }
}