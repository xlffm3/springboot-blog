package com.spring.blog.post.application.dto.response;

import com.spring.blog.post.domain.Post;
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
public class PostResponseDto {

    private Long id;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String author;
    private Long viewCounts;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static PostResponseDto from(Post post) {
        return PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .imageUrls(post.getImageUrls())
            .author(post.getAuthorName())
            .viewCounts(post.getViewCounts())
            .createdDate(post.getCreatedDate())
            .modifiedDate(post.getModifiedDate())
            .build();
    }
}
