package com.spring.s3proxy.web.domain;

public class StoreResult {

    private String originalFileName;
    private String url;

    private StoreResult() {
    }

    public StoreResult(String originalFileName, String url) {
        this.originalFileName = originalFileName;
        this.url = url;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getUrl() {
        return url;
    }
}
