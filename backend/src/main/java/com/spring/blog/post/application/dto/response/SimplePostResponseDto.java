package com.spring.blog.post.application.dto.response;

import com.spring.blog.post.domain.Post;
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
public class SimplePostResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;
    private Long viewCounts;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static SimplePostResponseDto from(Post post) {
        return SimplePostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .author(post.getAuthorName())
            .viewCounts(post.getViewCounts())
            .createdDate(post.getCreatedDate())
            .modifiedDate(post.getModifiedDate())
            .build();
    }
}
