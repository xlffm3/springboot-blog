package com.spring.s3proxy.web.infrastructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileNameGenerator {

    private static final Tika TIKA = new Tika();

    public String generateFileName(MultipartFile multipartFile, String userName) {
        String hashFileName = applyMD5(multipartFile, userName);
        String extension = detectFileExtension(multipartFile);
        return hashFileName + extension;
    }

    private String applyMD5(MultipartFile multipartFile, String userName) {
        String fileName = multipartFile.getOriginalFilename();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = (fileName + userName).getBytes(StandardCharsets.UTF_8);
            md.update(bytes);
            return Hex.encodeHexString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException();
        }
    }

    private String detectFileExtension(MultipartFile multipartFile) {
        try {
            MimeTypes defaultMimeTypes = MimeTypes.getDefaultMimeTypes();
            String target = TIKA.detect(multipartFile.getBytes());
            MimeType mimeType = defaultMimeTypes.forName(target);
            return mimeType.getExtension();
        } catch (IOException | MimeTypeException e) {
            throw new IllegalArgumentException();
        }
    }
}
