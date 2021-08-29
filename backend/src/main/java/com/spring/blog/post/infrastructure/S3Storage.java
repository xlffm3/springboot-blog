package com.spring.blog.post.infrastructure;

import com.spring.blog.exception.platform.PlatformHttpErrorException;
import com.spring.blog.post.domain.FileStorage;
import com.spring.blog.post.infrastructure.dto.FilesResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Repository
public class S3Storage implements FileStorage {

    private final String s3ProxyUrl;

    public S3Storage(@Value("${storage.s3.url}") String s3ProxyUrl) {
        this.s3ProxyUrl = s3ProxyUrl;
    }

    @Override
    public List<String> store(List<MultipartFile> files, String userName) {
        if (Objects.isNull(files) || files.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return WebClient.create()
                .post()
                .uri(s3ProxyUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(generateMultipartBody(files, userName))
                .retrieve()
                .bodyToMono(FilesResponse.class)
                .blockOptional()
                .orElseThrow(PlatformHttpErrorException::new)
                .getUrls();
        } catch (WebClientException webClientException) {
            throw new PlatformHttpErrorException();
        }
    }

    private MultiValueMap<String, Object> generateMultipartBody(
        List<MultipartFile> files,
        String userName
    ) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("userName", userName);
        files.forEach(file -> {
            try {
                Resource resource =
                    new FileSystemResource(file.getBytes(), file.getOriginalFilename());
                multiValueMap.add("files", resource);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
        return multiValueMap;
    }

    public static class FileSystemResource extends ByteArrayResource {

        private String fileName;

        public FileSystemResource(byte[] byteArray, String filename) {
            super(byteArray);
            this.fileName = filename;
        }

        @Override
        public String getFilename() {
            return fileName;
        }

        public void setFilename(String fileName) {
            this.fileName = fileName;
        }
    }
}
