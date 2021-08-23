package com.spring.s3proxy.common;

import com.spring.s3proxy.exception.format.FileExtensionException;
import java.io.IOException;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements FileValidator {

    private static final Tika TIKA = new Tika();
    private static final String REQUIRED_MIME_TYPE = "image";

    @Override
    public void validate(MultipartFile multipartFile) {
        try {
            String mimeType = TIKA.detect(multipartFile.getBytes());
            if (!mimeType.startsWith(REQUIRED_MIME_TYPE)) {
                throw new FileExtensionException();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
