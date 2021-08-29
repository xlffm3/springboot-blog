package com.spring.blog.comment.application.dto.response;

import com.spring.blog.comment.domain.Comment;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponseDto {

    private Long id;
    private String author;
    private String content;
    private Long depth;
    private LocalDateTime createdDate;

    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
            comment.getId(),
            comment.getAuthorName(),
            comment.getContent(),
            comment.getDepth(),
            comment.getCreatedDate()
        );
    }
}
