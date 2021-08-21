package com.spring.blog.comment.application.dto;

public class CommentReplyRequestDto {

    private Long postId;
    private Long userId;
    private Long commentId;
    private String content;

    private CommentReplyRequestDto() {
    }

    public CommentReplyRequestDto(Long postId, Long userId, Long commentId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.commentId = commentId;
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }
}
