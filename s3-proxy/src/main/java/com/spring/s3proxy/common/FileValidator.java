package com.spring.s3proxy.common;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

    void validate(MultipartFile multipartFile);
}
