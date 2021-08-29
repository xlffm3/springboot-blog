package com.spring.blog.post.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostListRequest {

    private Long page;
    private Long size;
    private Long pageBlockCounts;
    private String searchType;
    private String keyword;
}
