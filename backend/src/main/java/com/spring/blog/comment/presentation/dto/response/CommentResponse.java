package com.spring.blog.comment.presentation.dto.response;

import com.spring.blog.comment.application.dto.response.CommentResponseDto;
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
public class CommentResponse {

    private Long id;
    private String author;
    private String content;
    private Long depth;
    private LocalDateTime createdDate;

    public static CommentResponse from(CommentResponseDto commentResponseDto) {
        return new CommentResponse(
            commentResponseDto.getId(),
            commentResponseDto.getAuthor(),
            commentResponseDto.getContent(),
            commentResponseDto.getDepth(),
            commentResponseDto.getCreatedDate()
        );
    }
}
