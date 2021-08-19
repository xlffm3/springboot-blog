package com.spring.blog.post.presentation.dto;

public class PostRequest {

    private String title;
    private String content;

    private PostRequest() {
    }

    public PostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
