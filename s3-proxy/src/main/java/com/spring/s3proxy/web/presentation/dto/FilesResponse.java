package com.spring.s3proxy.web.presentation.dto;

import java.util.List;

public class FilesResponse {

    private List<String> urls;

    private FilesResponse() {
    }

    public FilesResponse(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }
}
