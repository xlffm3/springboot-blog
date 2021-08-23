package com.spring.blog.post.presentation;

import com.spring.blog.authentication.domain.Authenticated;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.post.application.PostService;
import com.spring.blog.post.application.dto.PostListRequestDto;
import com.spring.blog.post.application.dto.PostListResponseDto;
import com.spring.blog.post.application.dto.PostResponseDto;
import com.spring.blog.post.application.dto.PostWriteRequestDto;
import com.spring.blog.post.presentation.dto.PostListResponse;
import com.spring.blog.post.presentation.dto.PostResponse;
import com.spring.blog.post.presentation.dto.PostWriteRequest;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class PostController {

    private static final String REDIRECT_URL_FORMAT_AFTER_WRITING = "/api/posts/%d";

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> write(
        @Authenticated AppUser appUser,
        PostWriteRequest postWriteRequest
    ) {
        PostWriteRequestDto postWriteRequestDto = new PostWriteRequestDto(
            appUser.getId(),
            postWriteRequest.getTitle(),
            postWriteRequest.getContent(),
            postWriteRequest.getFiles()
        );
        PostResponseDto postResponseDto = postService.write(postWriteRequestDto);
        String url = String.format(REDIRECT_URL_FORMAT_AFTER_WRITING, postResponseDto.getId());
        return ResponseEntity.created(URI.create(url))
            .build();
    }

    @GetMapping("/posts")
    public ResponseEntity<PostListResponse> readList(
        @RequestParam Long page,
        @RequestParam Long size,
        @RequestParam Long pageBlockCounts
    ) {
        PostListRequestDto postListRequestDto = new PostListRequestDto(page, size, pageBlockCounts);
        PostListResponseDto postListResponseDto = postService.readPostList(postListRequestDto);
        PostListResponse postListResponse = PostListResponse.from(postListResponseDto);
        return ResponseEntity.ok(postListResponse);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponse> read(@PathVariable Long id) {
        PostResponseDto postResponseDto = postService.readById(id);
        PostResponse postResponse = PostResponse.from(postResponseDto);
        return ResponseEntity.ok(postResponse);
    }
}
