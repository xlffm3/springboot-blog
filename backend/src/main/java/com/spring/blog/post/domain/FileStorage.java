package com.spring.blog.post.domain;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    List<String> store(List<MultipartFile> files, String userName);
}
