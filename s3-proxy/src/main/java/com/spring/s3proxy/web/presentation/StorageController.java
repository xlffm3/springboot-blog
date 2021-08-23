package com.spring.s3proxy.web.presentation;

import com.spring.s3proxy.config.resolver.ValidExtension;
import com.spring.s3proxy.web.application.StorageService;
import com.spring.s3proxy.web.application.dto.FilesRequestDto;
import com.spring.s3proxy.web.application.dto.FilesResponseDto;
import com.spring.s3proxy.web.presentation.dto.FilesRequest;
import com.spring.s3proxy.web.presentation.dto.FilesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/storage")
    public ResponseEntity<FilesResponse> store(@ValidExtension FilesRequest filesRequest) {
        FilesRequestDto filesRequestDto =
            new FilesRequestDto(filesRequest.getUserName(), filesRequest.getFiles());
        FilesResponseDto filesResponseDto = storageService.store(filesRequestDto);
        FilesResponse filesResponse = new FilesResponse(filesResponseDto.getUrls());
        return ResponseEntity.ok(filesResponse);
    }
}
