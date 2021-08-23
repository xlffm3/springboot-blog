package com.spring.s3proxy.web.application;

import com.spring.s3proxy.web.application.dto.FilesRequestDto;
import com.spring.s3proxy.web.application.dto.FilesResponseDto;
import com.spring.s3proxy.web.domain.FileStorage;
import com.spring.s3proxy.web.domain.StoreResult;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    private final FileStorage fileStorage;

    public StorageService(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public FilesResponseDto store(FilesRequestDto filesRequestDto) {
        List<StoreResult> storeResults =
            fileStorage.store(filesRequestDto.getFiles(), filesRequestDto.getUserName());
        List<String> urls = storeResults.stream()
            .map(StoreResult::getUrl)
            .collect(Collectors.toList());
        return new FilesResponseDto(urls);
    }
}
