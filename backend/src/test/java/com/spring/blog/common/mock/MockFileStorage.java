package com.spring.blog.common.mock;

import static java.util.stream.Collectors.toList;

import com.spring.blog.post.domain.FileStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public class MockFileStorage implements FileStorage {

    @Override
    public List<String> store(List<MultipartFile> files, String userName) {
        if (Objects.isNull(files) || files.isEmpty()) {
            return new ArrayList<>();
        }
        return files.stream()
            .map(MultipartFile::getOriginalFilename)
            .collect(toList());
    }
}
