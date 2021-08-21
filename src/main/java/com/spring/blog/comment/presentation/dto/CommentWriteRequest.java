package com.spring.blog.comment.presentation.dto;

public class CommentWriteRequest {

    private String content;

    private CommentWriteRequest() {
    }

    public CommentWriteRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
