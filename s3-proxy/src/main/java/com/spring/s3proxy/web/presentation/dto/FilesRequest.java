package com.spring.s3proxy.web.presentation.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class FilesRequest {

    private String userName;
    private List<MultipartFile> files;

    private FilesRequest() {
    }

    public FilesRequest(String userName, List<MultipartFile> files) {
        this.userName = userName;
        this.files = files;
    }

    public String getUserName() {
        return userName;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }
}
