package com.spring.blog.comment.application.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentListRequestDto {

    private Long postId;
    private Long page;
    private Long size;
    private Long pageBlockCounts;
}
