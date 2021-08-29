package com.spring.blog.post.application.dto.request;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class PostWriteRequestDto {

    private Long userId;
    private String title;
    private String content;
    private List<MultipartFile> files;

    private PostWriteRequestDto() {
    }

    public PostWriteRequestDto(
        Long userId,
        String title,
        String content,
        List<MultipartFile> files
    ) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.files = files;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }
}
