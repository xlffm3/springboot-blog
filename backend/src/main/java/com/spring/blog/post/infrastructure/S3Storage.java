package com.spring.blog.post.infrastructure;

import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

import com.spring.blog.exception.platform.PlatformHttpErrorException;
import com.spring.blog.post.domain.FileStorage;
import com.spring.blog.post.infrastructure.dto.FilesResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
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
        try {
            return WebClient.create()
                .post()
                .uri(s3ProxyUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(fromMultipartData("userName", userName).with("files", files))
                .retrieve()
                .bodyToMono(FilesResponse.class)
                .blockOptional()
                .orElseThrow(PlatformHttpErrorException::new)
                .getUrls();
        } catch (WebClientException webClientException) {
            throw new PlatformHttpErrorException();
        }
    }
}
