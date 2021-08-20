package com.spring.blog.post.application.dto;

public class PostListRequestDto {

    private Long page;
    private Long size;
    private Long pageBlockCounts;

    private PostListRequestDto() {
    }

    public PostListRequestDto(Long page, Long size, Long pageBlockCounts) {
        this.page = page;
        this.size = size;
        this.pageBlockCounts = pageBlockCounts;
    }

    public Long getPage() {
        return page;
    }

    public Long getSize() {
        return size;
    }

    public Long getPageBlockCounts() {
        return pageBlockCounts;
    }
}
