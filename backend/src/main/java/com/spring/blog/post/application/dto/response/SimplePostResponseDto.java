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
        return new SimplePostResponseDto(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getAuthorName(),
            post.getViewCounts(),
            post.getCreatedDate(),
            post.getModifiedDate()
        );
    }
}
