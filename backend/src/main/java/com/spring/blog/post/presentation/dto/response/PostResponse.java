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
    private List<String> imageUrls;
    private String author;
    private Long viewCounts;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static PostResponse from(PostResponseDto postResponseDto) {
        return PostResponse.builder()
            .id(postResponseDto.getId())
            .title(postResponseDto.getTitle())
            .content(postResponseDto.getContent())
            .imageUrls(postResponseDto.getImageUrls())
            .author(postResponseDto.getAuthor())
            .viewCounts(postResponseDto.getViewCounts())
            .createdDate(postResponseDto.getCreatedDate())
            .modifiedDate(postResponseDto.getModifiedDate())
            .build();
    }
}
