package com.spring.blog.post.application.dto;

public class PostDeleteRequestDto {

    private Long postId;
    private Long userId;

    public PostDeleteRequestDto(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserId() {
        return userId;
    }
}
