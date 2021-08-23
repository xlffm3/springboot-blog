package com.spring.blog.post.presentation.dto;

import com.spring.blog.post.application.dto.PostResponseDto;
import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private List<String> urls;
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
        List<String> urls,
        String author,
        Long viewCounts,
        LocalDateTime createdDate,
        LocalDateTime modifiedDate
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.urls = urls;
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
            postResponseDto.getUrls(),
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

    public List<String> getUrls() {
        return urls;
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
