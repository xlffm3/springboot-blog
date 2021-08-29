package com.spring.blog.comment.application.dto.request;

public class CommentWriteRequestDto {

    private Long postId;
    private Long userId;
    private String content;

    private CommentWriteRequestDto() {
    }

    public CommentWriteRequestDto(Long postId, Long userId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }
}
