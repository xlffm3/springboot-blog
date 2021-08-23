package com.spring.blog.comment.application.dto;

import com.spring.blog.comment.domain.Comment;
import java.time.LocalDateTime;

public class CommentResponseDto {

    private Long id;
    private String author;
    private String content;
    private Long depth;
    private LocalDateTime createdDate;

    private CommentResponseDto() {
    }

    public CommentResponseDto(
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

    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
            comment.getId(),
            comment.getAuthorName(),
            comment.getContent(),
            comment.getDepth(),
            comment.getCreatedDate()
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
