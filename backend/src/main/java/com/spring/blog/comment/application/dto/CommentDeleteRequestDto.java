package com.spring.blog.comment.application.dto;

public class CommentDeleteRequestDto {

    private Long commentId;
    private Long userId;

    private CommentDeleteRequestDto() {
    }

    public CommentDeleteRequestDto(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getUserId() {
        return userId;
    }
}
