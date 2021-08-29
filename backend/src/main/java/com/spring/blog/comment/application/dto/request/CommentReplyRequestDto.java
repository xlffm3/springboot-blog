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
public class CommentReplyRequestDto {

    private Long postId;
    private Long userId;
    private Long commentId;
    private String content;
}
