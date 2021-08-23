package com.spring.blog.common.mock;

import static java.util.stream.Collectors.toList;

import com.spring.blog.post.domain.FileStorage;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class MockFileStorage implements FileStorage {

    @Override
    public List<String> store(List<MultipartFile> files, String userName) {
        return files.stream()
            .map(MultipartFile::getOriginalFilename)
            .collect(toList());
    }
}
