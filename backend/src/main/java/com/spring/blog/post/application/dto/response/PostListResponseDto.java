package com.spring.blog.post.application.dto.response;

import com.spring.blog.common.PageMaker;
import com.spring.blog.post.domain.Post;
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
public class PostListResponseDto {

    private List<SimplePostResponseDto> simplePostResponseDtos;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

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
}
