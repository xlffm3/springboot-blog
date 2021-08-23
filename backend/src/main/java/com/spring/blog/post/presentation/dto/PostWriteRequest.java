package com.spring.blog.post.presentation.dto;

public class PostWriteRequest {

    private String title;
    private String content;

    private PostWriteRequest() {
    }

    public PostWriteRequest(String title, String content) {
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
