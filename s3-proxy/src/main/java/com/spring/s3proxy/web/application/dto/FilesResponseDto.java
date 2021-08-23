package com.spring.s3proxy.web.application.dto;

import java.util.List;

public class FilesResponseDto {

    private List<String> urls;

    private FilesResponseDto() {
    }

    public FilesResponseDto(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }
}
