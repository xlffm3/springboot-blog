package com.spring.blog.post.presentation.dto.response;

import com.spring.blog.post.application.dto.response.PostListResponseDto;
import java.util.List;
import java.util.stream.Collectors;

public class PostListResponse {

    private List<PostResponse> postResponses;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    public PostListResponse() {
    }

    public PostListResponse(
        List<PostResponse> postResponses,
        int startPage,
        int endPage,
        boolean prev,
        boolean next
    ) {
        this.postResponses = postResponses;
        this.startPage = startPage;
        this.endPage = endPage;
        this.prev = prev;
        this.next = next;
    }

    public static PostListResponse from(PostListResponseDto postListResponseDto) {
        List<PostResponse> collect = postListResponseDto.getPostResponseDtos()
            .stream()
            .map(PostResponse::from)
            .collect(Collectors.toList());
        return new PostListResponse(
            collect,
            postListResponseDto.getStartPage(),
            postListResponseDto.getEndPage(),
            postListResponseDto.isPrev(),
            postListResponseDto.isNext()
        );
    }

    public List<PostResponse> getPostResponses() {
        return postResponses;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public boolean getPrev() {
        return prev;
    }

    public boolean getNext() {
        return next;
    }
}
