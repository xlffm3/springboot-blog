package com.spring.blog.post.presentation.dto;

import com.spring.blog.post.application.dto.PostResponseDto;
import java.time.LocalDateTime;

public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String author;
    private Long viewCounts;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;


    private PostResponse() {
    }

    public PostResponse(
        Long id,
        String title,
        String content,
        String author,
        Long viewCounts,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.viewCounts = viewCounts;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public static PostResponse from(PostResponseDto postResponseDto) {
        return new PostResponse(
            postResponseDto.getId(),
            postResponseDto.getTitle(),
            postResponseDto.getContent(),
            postResponseDto.getAuthor(),
            postResponseDto.getViewCounts(),
            postResponseDto.getCreatedDate(),
            postResponseDto.getModifiedDate()
        );
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

    public String getAuthor() {
        return author;
    }

    public Long getViewCounts() {
        return viewCounts;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
