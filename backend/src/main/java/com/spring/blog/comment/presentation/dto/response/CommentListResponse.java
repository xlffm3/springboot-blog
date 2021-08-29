package com.spring.blog.comment.presentation.dto.response;

import com.spring.blog.comment.application.dto.response.CommentListResponseDto;
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
public class CommentListResponse {

    private List<CommentResponse> commentResponses;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    public static CommentListResponse from(CommentListResponseDto commentListResponseDto) {
        List<CommentResponse> commentResponses = commentListResponseDto.getCommentResponseDtos()
            .stream()
            .map(CommentResponse::from)
            .collect(Collectors.toList());
        return new CommentListResponse(
            commentResponses,
            commentListResponseDto.getStartPage(),
            commentListResponseDto.getEndPage(),
            commentListResponseDto.isPrev(),
            commentListResponseDto.isNext()
        );
    }
}
