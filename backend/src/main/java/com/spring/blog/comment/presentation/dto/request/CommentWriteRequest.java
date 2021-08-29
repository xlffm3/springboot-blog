package com.spring.blog.comment.presentation.dto.request;

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
