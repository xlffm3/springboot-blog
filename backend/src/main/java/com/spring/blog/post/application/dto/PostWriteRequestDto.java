package com.spring.blog.post.application.dto;

public class PostWriteRequestDto {

    private Long userId;
    private String title;
    private String content;

    private PostWriteRequestDto() {
    }

    public PostWriteRequestDto(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
