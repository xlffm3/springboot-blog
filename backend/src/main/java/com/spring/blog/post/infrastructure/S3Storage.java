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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class S3Storage implements FileStorage {

    private final String s3ProxyUrl;
    private final WebClient webClient;

    public S3Storage(@Value("${storage.s3.url}") String s3ProxyUrl, WebClient webClient) {
        this.s3ProxyUrl = s3ProxyUrl;
        this.webClient = webClient;
    }

    @Override
    public List<String> store(List<MultipartFile> files, String userName) {
        if (Objects.isNull(files) || files.isEmpty()) {
            return new ArrayList<>();
        }
        return WebClient.create()
            .post()
            .uri(s3ProxyUrl)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(generateMultipartBody(files, userName))
            .retrieve()
            .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                .flatMap(errorMessage -> Mono.error(new PlatformHttpErrorException(errorMessage))))
            .bodyToMono(FilesResponse.class)
            .blockOptional()
            .orElseThrow(PlatformHttpErrorException::new)
            .getUrls();
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
