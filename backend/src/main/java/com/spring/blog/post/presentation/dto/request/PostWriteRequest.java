package com.spring.blog.post.presentation.dto.request;

import java.io.Serializable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class PostWriteRequest implements Serializable {

    private String title;
    private String content;
    private List<MultipartFile> files;

    private PostWriteRequest() {
    }

    public PostWriteRequest(String title, String content, List<MultipartFile> files) {
        this.title = title;
        this.content = content;
        this.files = files;
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
