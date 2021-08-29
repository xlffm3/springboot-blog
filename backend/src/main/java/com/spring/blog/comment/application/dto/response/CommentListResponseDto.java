package com.spring.blog.comment.application.dto.response;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.common.PageMaker;
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
public class CommentListResponseDto {

    private List<CommentResponseDto> commentResponseDtos;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    public static CommentListResponseDto from(List<Comment> comments, PageMaker pageMaker) {
        List<CommentResponseDto> commentResponseDtos = comments.stream()
            .map(CommentResponseDto::from)
            .collect(Collectors.toList());
        return new CommentListResponseDto(
            commentResponseDtos,
            pageMaker.getStartPage(),
            pageMaker.getEndPage(),
            pageMaker.isPrev(),
            pageMaker.isNext()
        );
    }
}
