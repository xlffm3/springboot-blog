package com.spring.s3proxy.web.domain;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    List<StoreResult> store(List<MultipartFile> files, String userName);
}
