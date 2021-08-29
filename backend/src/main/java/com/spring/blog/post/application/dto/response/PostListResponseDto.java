package com.spring.blog.post.application.dto.response;

import com.spring.blog.common.PageMaker;
import com.spring.blog.post.domain.Post;
import java.util.List;
import java.util.stream.Collectors;

public class PostListResponseDto {

    private List<SimplePostResponseDto> simplePostResponseDtos;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    private PostListResponseDto() {
    }

    public PostListResponseDto(
        List<SimplePostResponseDto> simplePostResponseDtos,
        int startPage,
        int endPage,
        boolean prev,
        boolean next
    ) {
        this.simplePostResponseDtos = simplePostResponseDtos;
        this.startPage = startPage;
        this.endPage = endPage;
        this.prev = prev;
        this.next = next;
    }

    public static PostListResponseDto from(List<Post> posts, PageMaker pageMaker) {
        List<SimplePostResponseDto> postResponseDtos = posts.stream()
            .map(SimplePostResponseDto::from)
            .collect(Collectors.toList());
        return new PostListResponseDto(
            postResponseDtos,
            pageMaker.getStartPage(),
            pageMaker.getEndPage(),
            pageMaker.isPrev(),
            pageMaker.isNext()
        );
    }

    public List<SimplePostResponseDto> getSimplePostResponseDtos() {
        return simplePostResponseDtos;
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
