package com.spring.blog.comment.application.dto;

public class CommentEditRequestDto {

    private Long commentId;
    private Long userId;
    private String content;

    private CommentEditRequestDto() {
    }

    public CommentEditRequestDto(Long commentId, Long userId, String content) {
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }
}
