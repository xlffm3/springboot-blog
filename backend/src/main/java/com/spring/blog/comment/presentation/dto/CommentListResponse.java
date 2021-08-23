package com.spring.blog.comment.presentation.dto;

import com.spring.blog.comment.application.dto.CommentListResponseDto;
import java.util.List;
import java.util.stream.Collectors;

public class CommentListResponse {

    private List<CommentResponse> commentResponses;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    private CommentListResponse() {
    }

    public CommentListResponse(
        List<CommentResponse> commentResponses,
        int startPage,
        int endPage,
        boolean prev,
        boolean next
    ) {
        this.commentResponses = commentResponses;
        this.startPage = startPage;
        this.endPage = endPage;
        this.prev = prev;
        this.next = next;
    }

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

    public List<CommentResponse> getCommentResponses() {
        return commentResponses;
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
