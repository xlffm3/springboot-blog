package com.spring.blog.post.presentation;

import com.spring.blog.authentication.domain.Authenticated;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.post.application.PostService;
import com.spring.blog.post.application.dto.request.PostDeleteRequestDto;
import com.spring.blog.post.application.dto.request.PostListRequestDto;
import com.spring.blog.post.application.dto.request.PostWriteRequestDto;
import com.spring.blog.post.application.dto.response.PostListResponseDto;
import com.spring.blog.post.application.dto.response.PostResponseDto;
import com.spring.blog.post.presentation.dto.PostListRequest;
import com.spring.blog.post.presentation.dto.request.PostWriteRequest;
import com.spring.blog.post.presentation.dto.response.PostListResponse;
import com.spring.blog.post.presentation.dto.response.PostResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PostController {

    private static final String REDIRECT_URL_FORMAT_AFTER_WRITING = "/api/posts/%d";

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<Void> write(
        @Authenticated AppUser appUser,
        PostWriteRequest postWriteRequest
    ) {
        PostWriteRequestDto postWriteRequestDto = PostWriteRequestDto.builder()
            .userId(appUser.getId())
            .title(postWriteRequest.getTitle())
            .content(postWriteRequest.getContent())
            .files(postWriteRequest.getFiles())
            .build();
        PostResponseDto postResponseDto = postService.write(postWriteRequestDto);
        String url = String.format(REDIRECT_URL_FORMAT_AFTER_WRITING, postResponseDto.getId());
        return ResponseEntity.created(URI.create(url))
            .build();
    }

    @GetMapping("/posts")
    public ResponseEntity<PostListResponse> readList(
        @ModelAttribute PostListRequest postListRequest
    ) {
        PostListRequestDto postListRequestDto = PostListRequestDto.builder()
            .page(postListRequest.getPage())
            .size(postListRequest.getSize())
            .pageBlockCounts(postListRequest.getPageBlockCounts())
            .searchType(postListRequest.getSearchType())
            .keyword(postListRequest.getKeyword())
            .build();
        PostListResponseDto postListResponseDto = postService.readPostList(postListRequestDto);
        PostListResponse postListResponse = PostListResponse.from(postListResponseDto);
        return ResponseEntity.ok(postListResponse);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> read(@PathVariable Long postId) {
        PostResponseDto postResponseDto = postService.readById(postId);
        PostResponse postResponse = PostResponse.from(postResponseDto);
        return ResponseEntity.ok(postResponse);
    }
    
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> delete(
        @PathVariable Long postId,
        @Authenticated AppUser appUser
    ) {
        PostDeleteRequestDto postDeleteRequestDto = PostDeleteRequestDto.builder()
            .postId(postId)
            .userId(appUser.getId())
            .build();
        postService.deletePost(postDeleteRequestDto);
        return ResponseEntity.noContent()
            .build();
    }
}
