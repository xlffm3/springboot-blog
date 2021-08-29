package com.spring.blog.post.presentation.dto.response;

import com.spring.blog.post.application.dto.response.SimplePostResponseDto;
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
public class SimplePostResponse {

    private Long id;
    private String title;
    private String content;
    private String author;
    private Long viewCounts;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static SimplePostResponse from(SimplePostResponseDto postResponseDto) {
        return SimplePostResponse.builder()
            .id(postResponseDto.getId())
            .title(postResponseDto.getTitle())
            .content(postResponseDto.getContent())
            .author(postResponseDto.getAuthor())
            .viewCounts(postResponseDto.getViewCounts())
            .createdDate(postResponseDto.getCreatedDate())
            .modifiedDate(postResponseDto.getModifiedDate())
            .build();
    }
}
