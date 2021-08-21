package com.spring.blog.comment.application.dto;

public class CommentWriteRequestDto {

    private Long postId;
    private Long authorId;
    private String content;

    private CommentWriteRequestDto() {
    }

    public CommentWriteRequestDto(Long postId, Long authorId, String content) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }
}
