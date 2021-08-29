package com.spring.blog.comment.application.dto.response;

import com.spring.blog.comment.domain.Comment;
import com.spring.blog.common.PageMaker;
import java.util.List;
import java.util.stream.Collectors;

public class CommentListResponseDto {

    private List<CommentResponseDto> commentResponseDtos;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    private CommentListResponseDto() {
    }

    public CommentListResponseDto(
        List<CommentResponseDto> commentResponseDtos,
        int startPage,
        int endPage,
        boolean prev,
        boolean next
    ) {
        this.commentResponseDtos = commentResponseDtos;
        this.startPage = startPage;
        this.endPage = endPage;
        this.prev = prev;
        this.next = next;
    }

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

    public List<CommentResponseDto> getCommentResponseDtos() {
        return commentResponseDtos;
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
