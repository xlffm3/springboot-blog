package com.spring.blog.post.application.dto;

import com.spring.blog.post.domain.Post;
import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private List<String> urls;
    private String author;
    private Long viewCounts;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    private PostResponseDto() {
    }

    public PostResponseDto(
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

    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getImageUrls(),
            post.getAuthorName(),
            post.getViewCounts(),
            post.getCreatedDate(),
            post.getModifiedDate()
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
