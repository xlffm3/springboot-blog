package com.spring.blog.post.presentation.dto.response;

import com.spring.blog.post.application.dto.response.PostListResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostListResponse {

    private List<SimplePostResponse> simplePostResponses;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

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
}
