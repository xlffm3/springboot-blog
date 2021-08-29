package com.spring.blog.post.presentation.dto.response;

import com.spring.blog.post.application.dto.response.PostListResponseDto;
import java.util.List;
import java.util.stream.Collectors;

public class PostListResponse {

    private List<SimplePostResponse> simplePostResponses;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    public PostListResponse() {
    }

    public PostListResponse(
        List<SimplePostResponse> simplePostResponses,
        int startPage,
        int endPage,
        boolean prev,
        boolean next
    ) {
        this.simplePostResponses = simplePostResponses;
        this.startPage = startPage;
        this.endPage = endPage;
        this.prev = prev;
        this.next = next;
    }

    public static PostListResponse from(PostListResponseDto postListResponseDto) {
        List<SimplePostResponse> simplePostResponses = postListResponseDto.getSimplePostResponseDtos()
            .stream()
            .map(SimplePostResponse::from)
            .collect(Collectors.toList());
        return new PostListResponse(
            simplePostResponses,
            postListResponseDto.getStartPage(),
            postListResponseDto.getEndPage(),
            postListResponseDto.isPrev(),
            postListResponseDto.isNext()
        );
    }

    public List<SimplePostResponse> getSimplePostResponses() {
        return simplePostResponses;
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
