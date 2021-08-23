package com.spring.s3proxy.web.application.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class FilesRequestDto {

    private String userName;
    private List<MultipartFile> files;

    private FilesRequestDto() {
    }

    public FilesRequestDto(String userName, List<MultipartFile> files) {
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
