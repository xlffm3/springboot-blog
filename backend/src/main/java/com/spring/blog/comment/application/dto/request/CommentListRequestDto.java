package com.spring.blog.comment.application.dto.request;

public class CommentListRequestDto {

    private Long postId;
    private Long page;
    private Long size;
    private Long pageBlockCounts;

    private CommentListRequestDto() {
    }

    public CommentListRequestDto(Long postId, Long page, Long size, Long pageBlockCounts) {
        this.postId = postId;
        this.page = page;
        this.size = size;
        this.pageBlockCounts = pageBlockCounts;
    }

    public Long getPostId() {
        return postId;
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
