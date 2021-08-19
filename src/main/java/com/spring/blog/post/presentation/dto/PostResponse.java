package com.spring.blog.post.presentation.dto;

public class PostResponse {

    private Long id;
    private String title;
    private String content;

    private PostResponse() {
    }

    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
