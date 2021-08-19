package com.spring.blog.post.presentation;

import com.spring.blog.authentication.domain.Authenticated;
import com.spring.blog.authentication.domain.user.AppUser;
import com.spring.blog.post.application.PostService;
import com.spring.blog.post.application.dto.PostRequestDto;
import com.spring.blog.post.application.dto.PostResponseDto;
import com.spring.blog.post.presentation.dto.PostRequest;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        @RequestBody PostRequest postRequest
    ) {
        PostRequestDto postRequestDto = new PostRequestDto(
            appUser.getId(),
            postRequest.getTitle(),
            postRequest.getContent()
        );
        PostResponseDto postResponseDto = postService.write(postRequestDto);
        String url = String.format(REDIRECT_URL_FORMAT_AFTER_WRITING, postResponseDto.getId());
        return ResponseEntity.created(URI.create(url))
            .build();
    }
}
