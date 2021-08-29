package com.spring.blog.post.presentation.dto.response;

import com.spring.blog.post.application.dto.response.PostResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private List<String> urls;
    private String author;
    private Long viewCounts;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

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
}
