package com.spring.blog.comment.presentation.dto;

import com.spring.blog.comment.application.dto.CommentResponseDto;
import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private String author;
    private String content;
    private Long depth;
    private LocalDateTime createdDate;

    private CommentResponse() {
    }

    public CommentResponse(
        Long id,
        String author,
        String content,
        Long depth,
        LocalDateTime createdDate
    ) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.depth = depth;
        this.createdDate = createdDate;
    }

    public static CommentResponse from(CommentResponseDto commentResponseDto) {
        return new CommentResponse(
            commentResponseDto.getId(),
            commentResponseDto.getAuthor(),
            commentResponseDto.getContent(),
            commentResponseDto.getDepth(),
            commentResponseDto.getCreatedDate()
        );
    }

    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Long getDepth() {
        return depth;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
}
