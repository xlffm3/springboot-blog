package com.spring.blog.post.application.dto.response;

import com.spring.blog.common.PageMaker;
import com.spring.blog.post.domain.Post;
import java.util.List;
import java.util.stream.Collectors;

public class PostListResponseDto {

    private List<PostResponseDto> postResponseDtos;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    private PostListResponseDto() {
    }

    public PostListResponseDto(
        List<PostResponseDto> postResponseDtos,
        int startPage,
        int endPage,
        boolean prev,
        boolean next
    ) {
        this.postResponseDtos = postResponseDtos;
        this.startPage = startPage;
        this.endPage = endPage;
        this.prev = prev;
        this.next = next;
    }

    public static PostListResponseDto from(List<Post> posts, PageMaker pageMaker) {
        List<PostResponseDto> postResponseDtos = posts.stream()
            .map(PostResponseDto::from)
            .collect(Collectors.toList());
        return new PostListResponseDto(
            postResponseDtos,
            pageMaker.getStartPage(),
            pageMaker.getEndPage(),
            pageMaker.isPrev(),
            pageMaker.isNext()
        );
    }

    public List<PostResponseDto> getPostResponseDtos() {
        return postResponseDtos;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public boolean isPrev() {
        return prev;
    }

    public boolean isNext() {
        return next;
    }
}
